package com.example.back.service.authMng.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.authMng.AuthMngMapper;
import com.example.back.service.authMng.AuthMngService;

@Service
public class AuthMngServiceImpl implements AuthMngService {

    @Autowired
    private AuthMngMapper authMngMapper;

    public List<Map<String, Object>> selectAuthMngList(Map<String, Object> paramsMap) {
        return authMngMapper.selectAuthMngList(paramsMap);
    }

    public int insert(Map<String, Object> saveMap) {
        return authMngMapper.insert(saveMap);
    }

    public int update(Map<String, Object> saveMap) {
        return authMngMapper.update(saveMap);
    }

    public int delete(Map<String, Object> delMap) {
        return authMngMapper.delete(delMap);
    }

}
