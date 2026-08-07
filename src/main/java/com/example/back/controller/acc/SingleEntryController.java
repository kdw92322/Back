package com.example.back.controller.acc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.service.acc.SingleEntryService;

@RestController
@RequestMapping("/singleEntry")
public class SingleEntryController {

    @Autowired
    private SingleEntryService singleEntryService;

    @GetMapping("/select")
    public List<Map<String, Object>> selectList(@RequestParam Map<String, Object> paramMap) {
        return singleEntryService.selectList(paramMap);
    }

    @PostMapping("/insert")
    public int insert(@RequestBody Map<String, Object> saveMap) {
        return singleEntryService.insert(saveMap);
    }

}
