package com.example.back.service.code.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.code.CodeGrpMapper;
import com.example.back.service.code.CodeGrpService;

@Service
public class CodeGrpServiceImpl implements CodeGrpService {

    @Autowired
    private CodeGrpMapper codeGrpMapper;

    public int insert(Map<String, Object> saveObj) {
        return codeGrpMapper.insert(saveObj);
    }

    public int update(Map<String, Object> saveObj) {
        return codeGrpMapper.update(saveObj);
    }

    public int delete(Map<String, Object> saveObj) {
        return codeGrpMapper.delete(saveObj);
    }
}
