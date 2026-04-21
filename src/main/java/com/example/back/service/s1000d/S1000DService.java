package com.example.back.service.s1000d;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import org.springframework.web.multipart.MultipartFile;

@Service
public class S1000DService {

     // 저장될 루트 경로 (예: application.properties에서 주입받아 사용 권장)
    @Value("${file.upload-dir}")
    private String uploadRoot;

    @Autowired
    private S1000DMapper s1000DMapper;
    
    public void storeUnzippedFiles(MultipartFile zipFile) throws IOException {
        // 1. 저장할 디렉토리 생성
        Path targetDir = Paths.get(uploadRoot).toAbsolutePath().normalize();
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // 2. ZIP 스트림 읽기 (한글 파일명 대응을 위해 필요시 Charset 지정)
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream(), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            
            while ((entry = zis.getNextEntry()) != null) {
                // 보안: Zip Slip 방지 및 경로 결합
                System.out.println("처리 중인 ZIP 엔트리: " + entry.getName()); // 디버깅용 로그

                Path filePath = targetDir.resolve(entry.getName()).normalize();
                System.out.println("처리 중인 파일: " + filePath); // 디버깅용 로그

                if (!filePath.startsWith(targetDir)) {
                    throw new IOException("유효하지 않은 파일 경로입니다: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    // 폴더인 경우 생성
                    Files.createDirectories(filePath);
                } else {
                    // 파일인 경우: 부모 폴더가 없으면 생성 후 파일 복사
                    if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
                        Files.createDirectories(filePath.getParent());
                    }
                    
                    // 핵심: 해당 경로에 파일 저장 (기존 파일 있을 시 덮어쓰기)
                    Files.copy(zis, filePath, StandardCopyOption.REPLACE_EXISTING);

                    //DB에도 파일 정보 저장
                    s1000DMapper.insertFileInfo(entry.getName(), filePath.toString(), entry.getSize());
                }
                zis.closeEntry();
            }
        }
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

    public String getContent(String relativePath) throws IOException {
        Path root = Paths.get(uploadRoot).toAbsolutePath().normalize();
        Path filePath = root.resolve(relativePath).normalize();

        // 보안: 루트 디렉토리 탈출 방지
        if (!filePath.startsWith(root)) {
            throw new SecurityException("접근 권한이 없는 경로입니다.");
        }

        if (!Files.exists(filePath)) {
            throw new IOException("파일을 찾을 수 없습니다: " + relativePath);
        }

        return Files.readString(filePath, StandardCharsets.UTF_8);
    }

    public JsonNode getJsonContent(String relativePath) throws IOException {
        String xmlContent = getContent(relativePath);
        
        return null;
    }
}
