package com.example.back.controller.s1000d;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.service.s1000d.S1000DService;

@RestController
public class S1000dResourceController {
    
    @Value("${file.upload-dir}")
    private String uploadRoot;

    @Autowired
    private S1000DService s1000DService;

    @GetMapping("/s1000d/image/{csdbId}/{imageLink}")
    public ResponseEntity<Resource> getIcnResource(@PathVariable String csdbId, @PathVariable String imageLink) throws IOException {
        Map<String, Object> param = new HashMap<>();
        param.put("csdbId", csdbId);
        List<Map<String, Object>> csdbList = s1000DService.selectCsdbList(param);

        if (csdbList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> csdb = csdbList.get(0);
        String name = String.valueOf(csdb.get("name"));

        System.out.println("csdbId : " + csdbId);
        //System.out.println("name : " + name);

        // S1000D 리소스 물리 경로 구성 (uploadRoot/csdbId/name/csdb/imageLink)
        imageLink = imageLink + ".svg"; // 요청된 이미지 링크에 .svg 확장자 추가
        Path path = Paths.get(uploadRoot, csdbId, "csdb", imageLink).toAbsolutePath().normalize();
        System.out.println("Path : " + path);

        // S1000DService에서 래스터 이미지를 SVG로 변환하여 저장했을 수 있으므로, 
        // 요청된 파일이 없고 확장자가 포함된 경우 .svg 파일로 다시 확인합니다.
        if (!Files.exists(path) && imageLink.contains(".")) {
            String baseName = imageLink.substring(0, imageLink.lastIndexOf("."));
            Path svgPath = path.getParent().resolve(baseName + ".svg");
            if (Files.exists(svgPath)) {
                path = svgPath;
            }
        }

        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(path.toUri());
        String contentType = Files.probeContentType(path);
        
        // SVG의 경우 브라우저 호환성을 위해 명시적으로 Content-Type 설정
        if (contentType == null || path.getFileName().toString().toLowerCase().endsWith(".svg")) {
            contentType = "image/svg+xml";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(resource);
    }
}
