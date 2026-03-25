package com.example.back.controller.code;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.back.service.code.CodeGrpService;

@RestController
@RequestMapping("/codeGrp")
public class CodeGrpController {
    
    @Autowired
    private CodeGrpService codeGrpService;

    @PostMapping("/insert")
    public int insert(@RequestBody Map<String, Object> saveObj) {
        System.out.println("saveObj : " + saveObj);
        int result = codeGrpService.insert(saveObj);
        return result;
    }

    @PutMapping("/update")
    public int update(@RequestBody Map<String, Object> saveObj) {
        System.out.println("saveObj : " + saveObj);
        int result = codeGrpService.update(saveObj);
        return result;
    }

    @DeleteMapping("/delete")
    public int delete(@RequestBody Map<String, Object> deleteObj) {
        System.out.println("deleteObj : " + deleteObj);
        int result = codeGrpService.delete(deleteObj);
        return result;
    }
}
