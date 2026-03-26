package com.example.back.service.menuAuth;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.menuAuth.MenuAuthMapper;

@Service
public class MenuAuthService {

    @Autowired
    private MenuAuthMapper menuAuthMapper;

    public List<Map<String, Object>> select(Map<String,Object> param) {
        return menuAuthMapper.select(param);
    }

    public int countAll() {
        return menuAuthMapper.countAll();
    }

    public int save(Map<String,Object> param) {
        System.out.println(param);

        String role_id = String.valueOf(param.get("role_id"));    

        return 1;
    }

    public int insert(Map<String,Object> param) {
        return menuAuthMapper.insert(param);
    }

}