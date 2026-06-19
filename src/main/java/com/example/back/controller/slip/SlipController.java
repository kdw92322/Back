package com.example.back.controller.slip;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.service.slip.SlipService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/slip")
public class SlipController {
    
    @Autowired
    private SlipService slipService;
    
    @GetMapping("/list")
    public List<Map<String, Object>> select(@RequestParam Map<String, Object> param) {
        return slipService.list(param);
    }
    
    @PostMapping("/insert")
    public int insert(@RequestBody Map<String, Object> saveObj) {
        slipService.insert(saveObj);
        return 0;
    }
    
}
