package com.example.back.service.s1000d;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.back.mapper.s1000d.S1000DMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.stream.Collectors;

@Service
public class S1000DService {

     // 저장될 루트 경로 (예: application.properties에서 주입받아 사용 권장)
    @Value("${file.upload-dir}")
    private String uploadRoot;

    @Autowired
    private S1000DMapper s1000DMapper;
    
    @Autowired
    private ObjectMapper objectMapper;

    public List<Map<String, Object>> selectCsdbList(Map<String, Object> param) throws IOException {
        return s1000DMapper.selectCsdbList(param);
    }

    @Transactional(rollbackFor = Exception.class)
    public void storeUnzippedFiles(MultipartFile zipFile) throws IOException {
        // 1. 절대 경로 정규화 및 디렉토리 생성
        Path targetDir = Paths.get(uploadRoot).toAbsolutePath().normalize();
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // 2. ZIP 스트림 처리 (Try-with-resources로 자원 반환 보장)
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream(), StandardCharsets.UTF_8)) {
            Map<String, Object> csdbInfo = new HashMap<>(); 
            
            String uuid = UUID.randomUUID().toString();
            String zipFileName = zipFile.getOriginalFilename();
            String size = formatSize(zipFile.getSize());
            
            csdbInfo.put("csdb_id", uuid);
            csdbInfo.put("filename", zipFileName);
            csdbInfo.put("filesize", size);      
            s1000DMapper.insertCsdbInfo(csdbInfo);

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                // 보안: Zip Slip 취약점 방지
                Path filePath = targetDir.resolve(entry.getName()).normalize();
                if (!filePath.startsWith(targetDir)) {
                    throw new IOException("유효하지 않은 파일 경로입니다: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    // 부모 디렉토리가 없으면 생성
                    if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
                        Files.createDirectories(filePath.getParent());
                    }
                    
                    // 파일 쓰기 (메모리 효율을 위해 대용량 파일은 스트림 복사를 권장하지만, 
                    // S1000D XML 특성상 readAllBytes를 사용해도 무방함)
                    byte[] fileBytes = zis.readAllBytes();
                    Files.write(filePath, fileBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                    // XML 파일인 경우 DB 메타데이터 저장
                    String fileName = filePath.getFileName().toString();
                    
                    System.out.println("처리 중인 파일: " + fileName); // 디버깅용 로그
                    if (fileName.toLowerCase().endsWith(".xml")) {
                        String dmcId = fileName.substring(0, fileName.lastIndexOf("."));
                        
                        // 1. 기존 방식대로 파일 바이트를 UTF-8 문자열로 읽어옵니다.
                        String xmlContent = new String(fileBytes, StandardCharsets.UTF_8);
                        
                        // 2. 혹시 모를 UTF-8 BOM(\ufeff) 문자가 붙어있다면 제거합니다.
                        if (xmlContent.startsWith("\ufeff")) {
                            xmlContent = xmlContent.substring(1);
                        }

                        // 3. XML 선언부(<?xml) 앞뒤에 붙은 미세한 공백과 줄바꿈을 제거합니다.
                        xmlContent = xmlContent.trim();

                        // 4. [선택 사항] 혹시 모를 정규식 안전장치 (선언문 앞의 보이지 않는 모든 특수문자 제거)
                        xmlContent = xmlContent.replaceFirst("^([\\W]+)<", "<");

                        Map<String, Object> fileInfo = new HashMap<>();
                        fileInfo.put("csdb_id", uuid);
                        fileInfo.put("dmcId", dmcId);
                        fileInfo.put("xmlContent", xmlContent);
                        fileInfo.put("filePath", filePath.toString());

                        // MyBatis/JPA를 통한 DB 저장 (Transactional에 의해 일관성 보장)
                        s1000DMapper.insertFileInfo(fileInfo);
                    }
                }
                zis.closeEntry();
            }
        }
        // 이 메서드가 종료되어야 컨트롤러에서 200 OK 응답을 보냅니다.
    }

    public List<Map<String, Object>> selectPmc() throws IOException {
        return s1000DMapper.selectPmc();
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
        param.keySet().forEach(key -> System.out.println("key : " + key)); // 디버깅용 로그
            Object xmlContent = param.get("ietmData");
            if (xmlContent instanceof String) {
                try {
                    // JSON 문자열을 JsonNode 객체로 변환하여 다시 맵에 저장
                    rtnMap.put("xmlContent", objectMapper.readTree((String) xmlContent));
                    System.out.println("convert JSON Object : " + rtnMap.get("xmlContent")); // 디버깅용 로그
                } catch (Exception e) {
                    // JSON 형식이 아닐 경우(일반 텍스트/XML) 원본 문자열 유지
                }
            }
        return rtnMap;
    }

    /**
     * 파일 크기(byte)를 읽기 쉬운 단위(KB, MB 등)로 변환합니다.
     */
    private String formatSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroup = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.2f %s", 
            size / Math.pow(1024, digitGroup), 
            units[digitGroup]);
    }

}
