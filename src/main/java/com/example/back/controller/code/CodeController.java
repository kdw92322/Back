package com.example.back.controller.code;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.service.code.CodeService;

@RestController
@RequestMapping("/code")
public class CodeController {
    
    @Autowired
    private CodeService codeService;


    @GetMapping("/select")
    public List<Map<String,Object>> select(@RequestParam Map<String, Object> request) {
        System.out.println("Received request: " + request);
        List<Map<String,Object>> result = codeService.selectMstCodeList(request);
        return result;
    }
}
