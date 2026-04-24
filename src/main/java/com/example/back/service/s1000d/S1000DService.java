package com.example.back.service.s1000d;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.ArrayList;

import com.example.back.mapper.s1000d.S1000DMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class S1000DService {

     // 저장될 루트 경로 (예: application.properties에서 주입받아 사용 권장)
    @Value("${file.upload-dir}")
    private String uploadRoot;

    @Autowired
    private S1000DMapper s1000DMapper;
    
    @Transactional(rollbackFor = Exception.class)
    public void storeUnzippedFiles(MultipartFile zipFile) throws IOException {
        // 1. 절대 경로 정규화 및 디렉토리 생성
        Path targetDir = Paths.get(uploadRoot).toAbsolutePath().normalize();
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // 2. ZIP 스트림 처리 (Try-with-resources로 자원 반환 보장)
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream(), StandardCharsets.UTF_8)) {
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
                    if (fileName.toLowerCase().endsWith(".xml")) {
                        String dmcId = fileName.substring(0, fileName.lastIndexOf("."));
                        String xmlContent = new String(fileBytes, StandardCharsets.UTF_8);

                        Map<String, Object> fileInfo = new HashMap<>();
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

    public List<Map<String, Object>> getModules(Map<String, Object> param) {
        Path root = Paths.get(uploadRoot).toAbsolutePath().normalize();
        if (!Files.exists(root)) {
            return new ArrayList<>();
        }

        try (Stream<Path> walk = Files.walk(root)) {
            return walk.filter(Files::isRegularFile)
                    .map(path -> {
                        Map<String, Object> fileInfo = new HashMap<>();
                        String fileName = path.getFileName().toString();
                        String relPath = root.relativize(path).toString().replace("\\", "/");
                        
                        fileInfo.put("fileName", fileName);
                        fileInfo.put("filePath", relPath);
                        fileInfo.put("fileSize", path.toFile().length());
                        
                        String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase() : "";
                        fileInfo.put("extension", ext);

                        // S1000D 모듈 타입 구분
                        if (fileName.startsWith("DMC-")) {
                            fileInfo.put("docType", "Data Module");
                        } else if (fileName.startsWith("PMC-")) {
                            fileInfo.put("docType", "Publication Module");
                        } else {
                            fileInfo.put("docType", "Resource");
                        }
                        return fileInfo;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Map<String, Object> getContent(Map<String, Object> param) throws IOException {
        Map<String, Object> contentInfo = new HashMap<>();
        
        String path = (String) param.get("path");
        Path root = Paths.get(uploadRoot).toAbsolutePath().normalize();
        Path filePath = root.resolve(path).normalize();

        // 보안: 루트 디렉토리 탈출 방지
        if (!filePath.startsWith(root)) {
            throw new SecurityException("접근 권한이 없는 경로입니다.");
        }

        if (!Files.exists(filePath)) {
            throw new IOException("파일을 찾을 수 없습니다: " + path);
        }

        System.out.println("param " + param); // 디버깅용 로그
        List<Map<String, Object>> Json1 = s1000DMapper.getDescriptiveDm1(param);
        List<Map<String, Object>> Json2 = s1000DMapper.getDescriptiveDm2(param);
        

        contentInfo.put("json1", Json1);
        contentInfo.put("json2", Json2);
        return contentInfo;
    }
}
