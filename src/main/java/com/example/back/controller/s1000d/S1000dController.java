package com.example.back.controller.s1000d;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.back.config.SseEmittersManager;
import com.example.back.service.s1000d.S1000DService;

import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/s1000d")
public class S1000dController {

    @Autowired
    private S1000DService s1000DService;
    // 연결된 클라이언트들의 SSE 세션을 안전하게 관리하기 위한 Map
    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 비동기 처리를 위한 스레드 풀
    @Autowired
    private SseEmittersManager emittersManager;
    
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private static final Logger log = LoggerFactory.getLogger(S1000dController.class);

    // 저장될 루트 경로 (예: application.properties에서 주입받아 사용 권장)
    @Value("${file.upload-dir}")
    private String uploadRoot;

    @GetMapping("/csdb/select")
    public List<Map<String, Object>> selectCsdbList(@RequestParam Map<String, Object> param) throws IOException {
        return s1000DService.selectCsdbList(param);
    }

    @PostMapping("/csdb/delete")
    public int deleteCsdb(@RequestBody Map<String, Object> param) throws IOException {
        return s1000DService.deleteCsdb(param);
    }

    @GetMapping("/csdb/xml-content")
    public Map<String, Object> xmlContent(@RequestParam Map<String, Object> param) throws IOException {
        return s1000DService.xmlContent(param);
    }

    // 1. SSE 모니터링 통로 개설 (GET)
    @GetMapping(value = "/connect/{uuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@PathVariable("uuid") String uuid) {
        SseEmitter emitter = new SseEmitter(1800000L); // 30분
        emittersManager.add(uuid, emitter);

        try {
            emitter.send(SseEmitter.event().name("connect").data("connected_success"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    // 2. 실제 ZIP 파일 업로드 및 비동기 압축 해제 (POST)
    @PostMapping("/upload")
    public ResponseEntity<String> uploadAndUnzip(
        @RequestParam("file") MultipartFile file,
        @RequestParam("uuid") String uuid) {

        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".zip")) {
            return ResponseEntity.badRequest().body("올바른 ZIP 파일이 아닙니다.");
        }

        try {
            // 1. 메인 스레드가 끝나서 임시 파일이 삭제되는 것을 방지하기 위해 
            // 별도의 로컬 반영구 임시 디렉토리에 진짜 파일로 복사(백업)해 둡니다.
            Path tempZipPath = Paths.get("./temp_uploads/" + uuid + "_" + file.getOriginalFilename());
            Files.createDirectories(tempZipPath.getParent());
            
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, tempZipPath, StandardCopyOption.REPLACE_EXISTING);
            }

            log.info("📌 ZIP 파일이 임시 저장된 절대 경로: {}", tempZipPath);

            // 2. 백그라운드 스레드 가동
            executor.execute(() -> {
                SseEmitter emitter = emittersManager.get(uuid);
                Path targetDir = Paths.get(uploadRoot + "/" + uuid);

                // ⭐ 크기 인덱스를 정확히 읽어오기 위해 ZipFile 객체 사용 (UTF-8 처리)
                try (ZipFile zipFile = new ZipFile(tempZipPath.toFile(), StandardCharsets.UTF_8)) {
                    Files.createDirectories(targetDir);

                    // 1차 패스: ZipFile은 개수를 전체 목록 조회가 가능하여 무척 간단함
                    int totalFilesCount = zipFile.size(); 
                    int currentCount = 0;

                    // 압축 내부 파일들을 순회하기 위한 Enumeration 구조 사용
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();

                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        currentCount++;

                        String fileName = entry.getName();
                        // ⭐ [해결] 데이터 디스크립터 압축 형태여도 무조건 정확한 실제 byte 크기가 반환됩니다.
                        long totalBytes = entry.getSize(); 

                        Path filePath = targetDir.resolve(fileName).normalize();

                        if (entry.isDirectory()) {
                            Files.createDirectories(filePath);
                            continue;
                        }

                        Files.createDirectories(filePath.getParent());

                        // 로그 생성 및 프론트 실시간 전송
                        String logMessage = String.format("📂 [압축 해제 중] (%d/%d) 파일명: %s (용량: %d bytes)", 
                                currentCount, totalFilesCount, fileName, totalBytes);
                        log.info(logMessage);

                        if (emitter != null) {
                            emitter.send(SseEmitter.event().name("unzip-log").data(logMessage));
                        }

                        // ⭐ ZipFile로부터 특정 Entry의 스트림을 직접 열어 압축 해제 처리
                        try (InputStream is = zipFile.getInputStream(entry);
                             FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                            
                            byte[] buffer = new byte[4096];
                            int len;
                            long writtenBytes = 0;
                            int lastPercent = 0;

                            while ((len = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                                writtenBytes += len;

                                // 이젠 totalBytes가 무조건 0보다 크므로 퍼센트 공식이 안정적으로 동작합니다.
                                if (totalBytes > 0) {
                                    int currentPercent = (int) ((writtenBytes * 100) / totalBytes);
                                    if (currentPercent > lastPercent) {
                                        lastPercent = currentPercent;
                                        if (currentPercent < 100) {
                                            // 실시간 % 전송 (매 퍼센트 변경 시마다 1번만 발송되도록 최적화됨)
                                            String progressLog = String.format("   ↳ ⏳ %s 진행률: %d%%", fileName, currentPercent);
                                            if (emitter != null) {
                                                emitter.send(SseEmitter.event().name("unzip-log").data(progressLog));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 모든 프로세스 끝남
                    if (emitter != null) {
                        emitter.send(SseEmitter.event().name("unzip-log").data("✨ 모든 파일의 압축 해제가 완벽하게 완료되었습니다!"));
                        emitter.complete();
                    }

                } catch (Exception e) {
                    log.error("압축 해제 실패: ", e);
                    if (emitter != null) {
                        try { emitter.send(SseEmitter.event().name("unzip-log").data("❌ 에러 발생으로 중단")); } catch (IOException ignored) {}
                        emitter.completeWithError(e);
                    }
                } finally {
                    // [필수] 작업이 다 끝났다면 서버 용량 확보를 위해 처음에 만들었던 임시 ZIP 백업본을 삭제합니다.
                    try {
                        Files.deleteIfExists(tempZipPath);
                    } catch (IOException e) {
                        log.warn("임시 파일 삭제 실패: {}", tempZipPath);
                    }
                }
            });

        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            return ResponseEntity.internalServerError().body("임시 파일 저장 실패");
        }

        return ResponseEntity.ok("압축 해제가 가동되었습니다.");    
    }
    /* 
    @PostMapping("/upload-csdb")
    public ResponseEntity<String> uploadAndUnzip(
            @RequestParam("file") MultipartFile file,
            @RequestParam("uuid") String uuid) {

        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".zip")) {
            return ResponseEntity.badRequest().body("올바른 ZIP 파일이 아닙니다.");
        }

        // 별도의 독립된 스레드에서 실제 압축 해제를 돌려 톰캣 블로킹 방지
        executor.execute(() -> {
            SseEmitter emitter = emittersManager.get(uuid);
            Path targetDir = Paths.get("./unzipped_files/" + uuid);

            try {
                Files.createDirectories(targetDir);
                
                // 1차 패스: 전체 파일 개수를 세기 위해 ZipInputStream을 생성해 순회
                int totalFilesCount = 0;
                try (ZipInputStream zisCount = new ZipInputStream(file.getInputStream(), StandardCharsets.UTF_8)) {
                    while (zisCount.getNextEntry() != null) {
                        totalFilesCount++;
                    }
                }

                // 2차 패스: 진짜 압축 해제 및 파일 쓰기 시작
                try (ZipInputStream zis = new ZipInputStream(file.getInputStream(), StandardCharsets.UTF_8)) {
                    ZipEntry entry;
                    int currentCount = 0;

                    while ((entry = zis.getNextEntry()) != null) {
                        currentCount++;
                        String fileName = entry.getName();
                        long totalBytes = entry.getSize();

                        Path filePath = targetDir.resolve(fileName).normalize();
                        
                        if (entry.isDirectory()) {
                            Files.createDirectories(filePath);
                            continue;
                        }

                        Files.createDirectories(filePath.getParent());

                        // 💡 로그 생성 및 출력
                        String logMessage = String.format("📂 [압축 해제 중] (%d/%d) 파일명: %s (용량: %d bytes)", 
                                currentCount, totalFilesCount, fileName, totalBytes);
                        log.info(logMessage);

                        // 프론트엔드로 로그 텍스트 실시간 전송
                        if (emitter != null) {
                            emitter.send(SseEmitter.event().name("unzip-log").data(logMessage));
                        }

                        // 버퍼 단위 실시간 쓰기 작업
                        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                            byte[] buffer = new byte[4096];
                            int len;
                            while ((len = zis.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                                // 필요 시 여기에 퍼센트(%) 계산 로직 추가 가능
                            }
                        }
                        zis.closeEntry();
                    }
                }

                // 최종 완료 전송 및 정상 마무리
                if (emitter != null) {
                    emitter.send(SseEmitter.event().name("unzip-log").data("✨ 모든 파일의 압축 해제가 완벽하게 완료되었습니다!"));
                    emitter.complete();
                }

            } catch (Exception e) {
                log.error("압축 해제 실패: ", e);
                if (emitter != null) {
                    try {
                        emitter.send(SseEmitter.event().name("unzip-log").data("❌ 에러 발생으로 인해 압축 해제가 중단되었습니다."));
                    } catch (IOException ignored) {}
                    emitter.completeWithError(e);
                }
            }
        });

        return ResponseEntity.ok("압축 해제 작업이 시작되었습니다.");
    }
    */

    @GetMapping("/pmc/select")
    public List<Map<String, Object>> selectPmc(@RequestParam Map<String, Object> param) throws IOException {
        System.out.println("PMC 선택 파라미터: " + param); // 디버깅용 로그
        return s1000DService.selectPmc(param);
    }

    @GetMapping("/pmc/tree")
    public Map<String, Object> pmcTree(@RequestParam Map<String, Object> param) throws IOException {
        return s1000DService.getXmlContentById(param);
    }

    @GetMapping("/dm/select")
    public Map<String, Object> selectDmc(@RequestParam Map<String, Object> param) throws IOException {
        return s1000DService.getXmlContentById(param);
    }

    @GetMapping("/getXmlContentByDmcId")
    public Map<String, Object> getXmlContentByDmcId(@RequestParam Map<String, Object> param) throws IOException {
        Map<String, Object> contents = s1000DService.getXmlContentById(param);
        // System.out.println("컨트롤러에서 반환할 모듈 리스트: " + rtnList); // 디버깅용 로그
        return contents;
    }

}
