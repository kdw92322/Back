package com.example.back.service.s1000d.impl;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Base64;

import com.example.back.mapper.s1000d.S1000DMapper;
import com.example.back.service.s1000d.S1000DService;
import com.example.back.service.s1000d.S1000DTransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.jcgm.core.CGM;
import net.sf.jcgm.core.CGMDisplay;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.stream.Stream;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.util.stream.Collectors;

@Service
public class S1000DServiceImpl implements S1000DService {

    // 💡 클래스 상단에 로거 객체를 선언합니다.
    private static final Logger log = LoggerFactory.getLogger(S1000DServiceImpl.class);

    public void testMethod() {
        log.info("일반적인 진행 안내 로그를 출력할 때");
        log.error("에러나 예외 상황을 강조하여 출력할 때");
    }

    @Autowired
    private S1000DTransactionService transactionService;

    // 저장될 루트 경로 (예: application.properties에서 주입받아 사용 권장)
    @Value("${file.upload-dir}")
    private String uploadRoot;

    private String CSDB_INDEX = "CSDB";

    @Autowired
    private S1000DMapper s1000DMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // 💡 서비스 전역에 스레드 세이프 맵 배치
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public List<Map<String, Object>> selectCsdbList(Map<String, Object> param) throws IOException {
        return s1000DMapper.selectCsdbList(param);
    }

    @Override
    public Map<String, Object> xmlContent(Map<String, Object> param) throws IOException {
        return s1000DMapper.xmlContent(param);
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteCsdb(Map<String, Object> param) throws IOException {
        String csdbId = (String) param.get("csdb_id");
        
        if (csdbId != null) {
            // 1. 해당 CSDB ID로 된 디렉토리 자체를 삭제 (uploadRoot/csdb_id)
            Path csdbDir = Paths.get(uploadRoot, csdbId, CSDB_INDEX).toAbsolutePath().normalize();
            
            if (Files.exists(csdbDir)) {
                try (Stream<Path> walk = Files.walk(csdbDir)) {
                    List<Path> pathsToDelete = walk.sorted(java.util.Comparator.reverseOrder())
                            .collect(Collectors.toList());
                    for (Path path : pathsToDelete) {
                        Files.delete(path);
                    }
                }
            }
        }

        s1000DMapper.deleteCsdb(param);
        return 0;
    }

    // 컨트롤러가 호출할 이미터 생성 메서드
    @Override
    public SseEmitter createEmitter(String clientId) {
        SseEmitter emitter = new SseEmitter(10 * 60 * 1000L); // 10분 설정
        emitters.put(clientId, emitter);

        log.info("🔌 [SSE 연결 완료] 새로운 클라이언트 세션 등록 ➡️ clientId: {}", clientId);

        emitter.onCompletion(() -> {
            log.info("❌ [SSE 연결 해제] 작업 완료로 인한 커넥션 자원 반환 ➡️ clientId: {}", clientId);
            emitters.remove(clientId);
        });
        emitter.onTimeout(() -> {
            log.warn("⏰ [SSE 연결 만료] 타임아웃 발생으로 연결 해제 ➡️ clientId: {}", clientId);
            emitters.remove(clientId);
        });
        emitter.onError((e) -> {
            log.error("💥 [SSE 연결 에러] 네트워크 스트림 예외 발생 ➡️ clientId: {}", clientId, e);
            emitters.remove(clientId);
        });

        try {
            // 커넥션 개방 신호 발송
            emitter.send(SseEmitter.event().name("init").data("connected"));
        } catch (IOException e) {
            emitters.remove(clientId);
        }
        return emitter;
    }

    // S1000DServiceImpl.java 내부에 구성할 진입점 메서드 스펙
    @Override
    @Async("taskExecutor")
    public void storeUnzippedFilesAsync(byte[] fileBytes, String originalFilename, long zipFileSize, String clientId) {
        // 💡 3. 비동기 스레드가 깨어난 직후 맵에서 직접 Emitter를 매핑해 가져옵니다.
        SseEmitter emitter = emitters.get(clientId);

        log.info("🚀 [백그라운드 스레드 가동] 처리 시작 ➡️ 파일명: {}, 클라이언트ID: {}", originalFilename, clientId);
        
        if (emitter == null) {
            // 🔍 중요 체크 포인트: 만약 콘솔에 이 워닝이 찍힌다면 React가 보내준 clientId 문자열과 SSE 연결할 때 쓴 ID가 불일치하는 상태입니다.
            log.warn("⚠️ [경고] clientId [{}] 에 매핑된 활성화된 SseEmitter를 찾지 못했습니다. 로그 전송이 생략됩니다.", clientId);
        }

        try {
            try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
                // 실시간 압축 풀기 및 인서트 핵심 비즈니스 로직 가동
                storeUnzippedFiles(inputStream, originalFilename, zipFileSize, emitter, fileBytes); 
            }

            log.info("🏁 [백그라운드 스레드 정상 종료] 모든 데이터 이관 완료 ➡️ clientId: {}", clientId);
            if (emitter != null) {
                emitter.send(SseEmitter.event().name("upload-success").data("CSDB 패키지 데이터 처리가 완료되었습니다."));
            }
        } catch (Exception e) {
            log.error("❌ [비동기 스레드 런타임 에러 발생]", e); // 에러 발생 시 자바 콘솔에 StackTrace 전체 출력하도록 로깅 처리

            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event().name("upload-failure").data("백엔드 예외: " + e.getMessage()));
                } catch (IOException ignored) {}
            }
        } finally {
            if (emitter != null) {
                emitter.complete();
            }
            emitters.remove(clientId);
        }
    }

    @Override
    public void storeUnzippedFiles(InputStream zipInputStream, String zipFileName, Long zipFileSize, 
                                   SseEmitter emitter, byte[] fileBytes) throws IOException {
        String uuid = UUID.randomUUID().toString();
        int totalFilesCount = countFilesInZip(fileBytes);
        int currentCount = 0;

        log.info("📊 [CSDB 인덱싱 초기화] 분석 대상 내부 파일 총 개수: {}개", totalFilesCount);

        Path targetDir = Paths.get(uploadRoot, uuid).toAbsolutePath().normalize();
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        Map<String, Object> csdbInfo = new HashMap<>();
        csdbInfo.put("csdb_id", uuid);
        csdbInfo.put("filename", zipFileName);
        csdbInfo.put("filesize", formatSize(zipFileSize));
        transactionService.insertCsdbMasterInfo(csdbInfo);

        try (ZipInputStream zis = new ZipInputStream(zipInputStream, StandardCharsets.UTF_8)) {
            ZipEntry entry;
            
            log.info("▶️ [ZipInputStream 루프 시동] 파일 복사 및 가공 처리를 전개합니다.");

            while ((entry = zis.getNextEntry()) != null) {
                Path filePath = targetDir.resolve(entry.getName()).normalize();
                
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
                        Files.createDirectories(filePath.getParent());
                    }

                    if (entry.getName().contains("__MACOSX")) {
                        zis.closeEntry();
                        continue;
                    }

                    currentCount++;
                    String fileKey = "file_" + currentCount;
                    long totalBytes = entry.getSize();
                    String fileName = filePath.getFileName().toString();

                    log.info("📂 [압축 해제 중] ({}/{}) 처리 중인 파일명: {} (용량: {} bytes)", currentCount, totalFilesCount, fileName, totalBytes);

                    // 기존에 작동이 검증된 분할 버퍼 파일 쓰기 처리 
                    try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                        byte[] buffer = new byte[4096];
                        int len;
                        long writtenBytes = 0;
                        int lastPercent = 0;

                        while ((len = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                            writtenBytes += len;

                            // 🟢 개선된 코드: 퍼센트가 진짜로 '증가'했을 때 딱 1번만 발송
                            if (totalBytes > 0) {
                                int currentPercent = (int) ((writtenBytes * 100) / totalBytes);
                                
                                // 현재 퍼센트가 이전 기록(lastPercent)보다 '확실히 큰 경우'에만 진입
                                if (currentPercent > lastPercent) {
                                    lastPercent = currentPercent; // 갱신하여 중복 발송 차단
                                    
                                    if (currentPercent < 100) {
                                        sendProgress(emitter, fileKey, fileName, currentPercent, currentCount, totalFilesCount);
                                    }
                                }
                            }
                        }
                    }

                    // 파일 물리 가공 및 단일 격리 트랜잭션 매퍼 실행 위임
                    byte[] fileBytesArray = Files.readAllBytes(filePath);
                    transactionService.processAndInsertFileInfo(uuid, fileName, filePath.toString(), fileBytesArray);

                    // 💡 개별 파일 인서트 최종 완료 100% 전송
                    sendProgress(emitter, fileKey, fileName, 100, currentCount, totalFilesCount);
                }
                zis.closeEntry();
            }
            log.info("🏁 [ZipInputStream 루프 종료] 모든 모듈 파일 시스템 동기화 및 DB 매핑 완료.");
        }
    }

    private void sendProgress(SseEmitter emitter, String fileKey, String fileName, int percent, int current, int total) {
        if (emitter == null) return;
        try {
            String payload = String.format(
                "{\"fileKey\":\"%s\",\"fileName\":\"%s\",\"percent\":%d,\"current\":%d,\"total\":%d}",
                fileKey, fileName, percent, current, total
            );
            // React의 fetchEventSource를 타겟으로 progress 이벤트를 밀어냅니다.
            emitter.send(SseEmitter.event().name("progress").data(payload));
        } catch (Exception e) {
            log.error("❌ [SSE 발송 실패] 클라이언트 스트림에 데이터를 쓰지 못했습니다. 메시지: {}", e.getMessage());
        }
    }

    /**
     * 파일 크기(byte)를 읽기 쉬운 단위(KB, MB 등)로 변환합니다.
     */
    private String formatSize(long bytes) {
        if (bytes <= 0) return "0 KB";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", (double) bytes / 1024);
        return String.format("%.1f MB", (double) bytes / (1024 * 1024));
    }

    // 파일 개수 카운트 유틸 메서드
    private int countFilesInZip(byte[] fileBytes) throws IOException {
        int count = 0;
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(fileBytes), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && !entry.getName().contains("__MACOSX")) {
                    count++;
                }
                zis.closeEntry();
            }
        }
        return count;
    }

    public List<Map<String, Object>> selectPmc(Map<String, Object> param) throws IOException {
        return s1000DMapper.selectPmc(param);
    }

    public Map<String, Object> getXmlContentById(Map<String, Object> param) throws IOException {
        List<Map<String, Object>> resultList = s1000DMapper.getXmlContentById(param);
        Map<String, Object> rtnMap = new HashMap<>();

        for (Map<String, Object> row : resultList) {
            rtnMap = convertStrToJson(row);
        }

        return rtnMap;
    }

    public Map<String, Object> convertStrToJson(Map<String, Object> param) throws IOException {
        Map<String, Object> rtnMap = new HashMap<>();
        Object xmlContent = param.get("ietmData");
        if (xmlContent instanceof String) {
            try {
                // JSON 문자열을 JsonNode 객체로 변환하여 다시 맵에 저장
                rtnMap.put("xmlContent", objectMapper.readTree((String) xmlContent));
                // System.out.println("convert JSON Object : " + rtnMap.get("xmlContent")); //
                // 디버깅용 로그
            } catch (Exception e) {
                // JSON 형식이 아닐 경우(일반 텍스트/XML) 원본 문자열 유지
            }
        }
        return rtnMap;
    }

}
