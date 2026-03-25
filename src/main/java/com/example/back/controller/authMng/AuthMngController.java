package com.example.back.controller.authMng;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.service.authMng.AuthMngService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController 
@RequestMapping("/authMng")   
public class AuthMngController {
    
    @Autowired
    private AuthMngService authMngService;

    @GetMapping("/select")
    public List<Map<String, Object>> selectAuthMngList(@RequestParam Map<String, Object> paramsMap) {
        return authMngService.selectAuthMngList(paramsMap);
    }

    @PostMapping("save")
    public int save(@RequestBody Map<String, Object> saveMap) {
        ObjectMapper mapper = new ObjectMapper();
        int result = 0;

        //1. insert
        List<Map<String, Object>> inserts = mapper.convertValue(
            saveMap.get("inserts"),
            new TypeReference<List<Map<String, Object>>>() {}
        );
        for (Map<String, Object> data : inserts) {
            result += authMngService.insert(data);
        }

        //2. update
        List<Map<String, Object>> updates = mapper.convertValue(
            saveMap.get("updates"),
            new TypeReference<List<Map<String, Object>>>() {}
        );
        for (Map<String, Object> data : updates) {
            result += authMngService.update(data);
        }

        return result;
    }


    @DeleteMapping("delete")
    public int delete(@RequestBody Map<String, Object> delMap) {
        return authMngService.delete(delMap);
    }
}
