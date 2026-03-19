package com.example.back.service.code;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.code.CodeMapper;

@Service
public class CodeService {

    @Autowired
	private CodeMapper codemapper;

    public List<Map<String,Object>> selectMstCodeList(Map<String,Object> paramMap) {
        return codemapper.selectMstCodeList(paramMap);
    }
}
