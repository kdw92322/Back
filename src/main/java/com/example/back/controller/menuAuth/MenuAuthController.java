package com.example.back.controller.menuAuth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.service.menuAuth.MenuAuthService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/menuAuth")   
public class MenuAuthController {

    @Autowired
    private MenuAuthService menuAuthService;

    @GetMapping("/select")
    public List<Map<String, Object>> select(@RequestParam Map<String, Object> param) {
        return menuAuthService.select(param);
    }
    
    @PostMapping("/insert")
    public int insert(@RequestBody Map<String, Object> param) {
        return menuAuthService.insert(param);
    }

    @PostMapping("/update")
    public int update(@RequestBody Map<String, Object> param) {
        return menuAuthService.update(param);
    }
}