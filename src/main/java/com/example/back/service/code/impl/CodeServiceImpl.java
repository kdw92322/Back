package com.example.back.service.code.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.back.mapper.code.CodeMapper;
import com.example.back.service.code.CodeService;

@Service
public class CodeServiceImpl implements CodeService {

    @Autowired
    private CodeMapper codemapper;

    public List<Map<String, Object>> selectMstCodeList(Map<String, Object> paramMap) {
        return codemapper.selectMstCodeList(paramMap);
    }

    public List<Map<String, Object>> selectDtlCodeList(Map<String, Object> paramMap) {
        return codemapper.selectDtlCodeList(paramMap);
    }

    public int insert(Map<String, Object> saveObj) {
        return codemapper.insert(saveObj);
    }

    public int update(Map<String, Object> saveObj) {
        return codemapper.update(saveObj);
    }

    public int delete(Map<String, Object> delObj) {
        return codemapper.delete(delObj);
    }
}
