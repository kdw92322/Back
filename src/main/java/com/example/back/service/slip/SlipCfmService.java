package com.example.back.service.slip;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

public interface SlipCfmService {

    public List<Map<String, Object>> list(Map<String, Object> param);

    public List<Map<String, Object>> details(Map<String, Object> param);

    public int approve(Map<String, Object> param);

    public int reject(Map<String, Object> param);

    @Transactional
    public int confirm(Map<String, Object> param);

}
