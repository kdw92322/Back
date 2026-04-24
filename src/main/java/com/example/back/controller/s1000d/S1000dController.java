package com.example.back.controller.s1000d;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.back.service.s1000d.S1000DService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/s1000d")
public class S1000dController {
    
    @Autowired
    private S1000DService s1000DService;

    @PostMapping("/upload-csdb")
    public ResponseEntity<String> uploadAndUnzip(@RequestParam("file") MultipartFile file) {
        try {
            s1000DService.storeUnzippedFiles(file);
            return ResponseEntity.ok("모든 파일이 업로드 경로에 저장되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("오류 발생: " + e.getMessage());
        }
    }
    
    
    @GetMapping("/test")
    public List<Map<String, Object>> test(@RequestParam Map<String, Object> params) {
        System.out.println("컨트롤러에서 받은 파라미터: " + params); // 디버깅용 로그
        return null;
    }

    @GetMapping("/getList")
    public List<Map<String, Object>> getList(@RequestParam Map<String, Object> param) {
        List<Map<String, Object>> rtnList = s1000DService.getModules(param);
        //System.out.println("컨트롤러에서 반환할 모듈 리스트: " + rtnList); // 디버깅용 로그
        return rtnList;
    }
    
    @PostMapping("/getContent")
    public ResponseEntity<Map<String, Object>> getContent(@RequestBody Map<String, Object> param) {
        try {
            Map<String, Object> content = s1000DService.getContent(param);
            return ResponseEntity.ok(content);
        } catch (IOException | SecurityException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

}
