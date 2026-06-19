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
import java.util.Base64;

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
    public int deleteCsdb(Map<String, Object> param) throws IOException {
        String csdbId = (String) param.get("csdb_id");
        
        if (csdbId != null) {
            // 1. 해당 CSDB ID로 된 디렉토리 자체를 삭제 (uploadRoot/csdb_id)
            Path csdbDir = Paths.get(uploadRoot, csdbId).toAbsolutePath().normalize();
            if (Files.exists(csdbDir)) {
                try (Stream<Path> walk = Files.walk(csdbDir)) {
                    List<Path> pathsToDelete = walk.sorted(java.util.Comparator.reverseOrder()).collect(Collectors.toList());
                    for (Path path : pathsToDelete) {
                        Files.delete(path);
                    }
                }
            }
        }
        // 2. DB 데이터 삭제 (Foreign Key 설정에 따라 연관 데이터도 삭제됨)
        return s1000DMapper.deleteCsdb(param);
    }

    @Transactional(rollbackFor = Exception.class)
    public void storeUnzippedFiles(MultipartFile zipFile) throws IOException {
        String uuid = UUID.randomUUID().toString();

        // 1. CSDB ID 전용 디렉토리 생성 (uploadRoot)
        Path targetDir = Paths.get(uploadRoot, uuid).toAbsolutePath().normalize();
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // 2. ZIP 스트림 처리 (Try-with-resources로 자원 반환 보장)
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream(), StandardCharsets.UTF_8)) {
            Map<String, Object> csdbInfo = new HashMap<>();
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
                    
                    // 1. 파일 데이터 읽기 (물리적 저장은 하단 조건에 따라 수행)
                    byte[] fileBytes = zis.readAllBytes();

                    String fileName = filePath.getFileName().toString();
                    String fileNameLower = fileName.toLowerCase();

                    System.out.println("처리 중인 파일: " + fileName); // 디버깅용 로그
                    if (fileNameLower.endsWith(".xml")) {
                        // XML 파일은 물리적으로 저장
                        Files.write(filePath, fileBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

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

                        System.out.println("filepath : " + filePath.toString()); // 디버깅용 로그

                        // MyBatis/JPA를 통한 DB 저장 (Transactional에 의해 일관성 보장)
                        s1000DMapper.insertFileInfo(fileInfo);
                    } else if (isIcnFile(fileNameLower)) {
                        // ICN 파일(이미지 등) 처리
                        String icnId = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;

                        if (fileNameLower.endsWith(".svg")) {
                            // 이미 SVG인 경우 저장
                            Files.write(filePath, fileBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                        } else if (isSupportedRaster(fileNameLower)) {
                            // 래스터 이미지를 SVG로 전환 처리
                            byte[] processedBytes = convertToSvg(fileBytes, fileName);
                            String targetFileName = icnId + ".svg";
                            
                            // 변환된 SVG 파일만 물리적으로 저장
                            Path svgPath = filePath.getParent().resolve(targetFileName);
                            Files.write(svgPath, processedBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                            System.out.println("SVG 변환 파일 저장 완료: " + svgPath);
                            
                            // 원본(PNG, JPG 등)은 저장하지 않음
                        }
                    }
                }
                zis.closeEntry();
            }
        }
        // 이 메서드가 종료되어야 컨트롤러에서 200 OK 응답을 보냅니다.
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
                    //System.out.println("convert JSON Object : " + rtnMap.get("xmlContent")); // 디버깅용 로그
                } catch (Exception e) {
                    // JSON 형식이 아닐 경우(일반 텍스트/XML) 원본 문자열 유지
                }
            }
        return rtnMap;
    }

    /**
     * SVG로 변환 가능한 래스터 이미지 포맷인지 확인합니다.
     */
    private boolean isSupportedRaster(String fileNameLower) {
        return fileNameLower.endsWith(".png") || fileNameLower.endsWith(".jpg") || 
               fileNameLower.endsWith(".jpeg") || fileNameLower.endsWith(".gif")||fileNameLower.endsWith(".cgm");
    }

    /**
     * 이미지 파일을 SVG 형식으로 전환합니다.
     * Raster 이미지(PNG, JPG 등)의 경우 브라우저 호환성을 위해 SVG <image> 태그로 래핑합니다.
     */
    private byte[] convertToSvg(byte[] fileBytes, String fileName) {
        String fileNameLower = fileName.toLowerCase();
        
        // 지원되는 래스터 이미지 확장자인 경우 처리
        if (fileNameLower.endsWith(".png") || fileNameLower.endsWith(".jpg") || 
            fileNameLower.endsWith(".jpeg") || fileNameLower.endsWith(".gif")) {
            try {
                String base64Content = Base64.getEncoder().encodeToString(fileBytes);
                String mimeType = fileNameLower.endsWith(".png") ? "image/png" : 
                                 (fileNameLower.endsWith(".gif") ? "image/gif" : "image/jpeg");
                
                String svgWrapper = String.format(
                    "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" viewBox=\"0 0 800 600\">" +
                    "<image width=\"100%%\" height=\"100%%\" xlink:href=\"data:%s;base64,%s\"/></svg>", mimeType, base64Content);
                return svgWrapper.getBytes(StandardCharsets.UTF_8);
            } catch (Exception e) {
                System.err.println("SVG 변환 중 오류 발생 (" + fileName + "): " + e.getMessage());
            }
        }
        // CGM, PDF 등 전용 변환 라이브러리가 필요한 포맷은 원본 데이터를 유지합니다.
        return fileBytes;
    }

    /**
     * 파일명이 S1000D ICN(이미지) 확장자인지 확인합니다.
     */
    private boolean isIcnFile(String fileName) {
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
               fileName.endsWith(".svg") || fileName.endsWith(".gif") || fileName.endsWith(".tif") || 
               fileName.endsWith(".tiff") || fileName.endsWith(".cgm") || fileName.endsWith(".pdf") ||
               fileName.endsWith(".cgm");
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
