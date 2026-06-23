package com.example.back.service.acc.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.acc.AccGLMapper;
import com.example.back.service.acc.AccGLService;

@Service
public class AccGLServiceImpl implements AccGLService {

    @Autowired
    private AccGLMapper accGLMapper;

    public List<Map<String, Object>> selectGLAccList(Map<String, Object> param) {
        return accGLMapper.selectGLAccList(param);
    }

    public List<Map<String, Object>> selectGLList(Map<String, Object> param) {
        return accGLMapper.selectGLList(param);
    }

}
