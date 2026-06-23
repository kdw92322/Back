package com.example.back.service.slip;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

public interface SlipService {

    public List<Map<String, Object>> list(Map<String, Object> param);

    @Transactional
    public int insert(Map<String, Object> saveObj);
}
