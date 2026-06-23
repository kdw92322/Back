package com.example.back.controller.acc;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.service.acc.AccGLService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/accgl")
public class AccGLController {

    @Autowired
    private AccGLService accGLService;

    @GetMapping("selectGLAccList")
    public List<Map<String, Object>> selectGLAccList(@RequestParam Map<String, Object> param) {
        return accGLService.selectGLAccList(param);
    }

    @GetMapping("select")
    public List<Map<String, Object>> select(@RequestParam Map<String, Object> param) {
        return accGLService.selectGLList(param);
    }
}