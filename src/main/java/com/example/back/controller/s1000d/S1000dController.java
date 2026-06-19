package com.example.back.controller.s1000d;

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

    @GetMapping("/csdb/select")
    public List<Map<String, Object>> selectCsdbList(@RequestParam Map<String, Object> param) throws IOException {
        return s1000DService.selectCsdbList(param);
    }

    @PostMapping("/csdb/delete")
    public int deleteCsdb(@RequestBody Map<String, Object> param) throws IOException {
        return s1000DService.deleteCsdb(param);
    }

    @PostMapping("/upload-csdb")
    public ResponseEntity<String> uploadAndUnzip(@RequestParam("file") MultipartFile file) {
        try {
            s1000DService.storeUnzippedFiles(file);
            return ResponseEntity.ok("모든 파일이 업로드 경로에 저장되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("오류 발생: " + e.getMessage());
        }
    }
    
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
        //System.out.println("컨트롤러에서 반환할 모듈 리스트: " + rtnList); // 디버깅용 로그
        return contents;
    }
    
}
