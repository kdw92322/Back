package com.example.back.controller.slip;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.service.slip.SlipCfmService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/slipCfm")
public class SlipCfmController {
    
    @Autowired
    private SlipCfmService slipCfmService;

    @GetMapping("list")
    public List<Map<String, Object>> list(@RequestParam Map<String, Object> param) {
        return slipCfmService.list(param);
    }
    
    @GetMapping("details")
    public List<Map<String, Object>> details(@RequestParam Map<String, Object> param) {
        return slipCfmService.details(param);
    }
    
    @PostMapping("approve")
    public int approve(@RequestBody Map<String, Object> param) {
        return slipCfmService.approve(param);
    }
    
    @PostMapping("reject")
    public int reject(@RequestBody Map<String, Object> param) {
        return slipCfmService.reject(param);
    }
    
    @PostMapping("confirm")
    public int confirm(@RequestBody Map<String, Object> param) {
        return slipCfmService.confirm(param);
    }


}
