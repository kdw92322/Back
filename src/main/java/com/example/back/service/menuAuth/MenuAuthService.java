package com.example.back.service.menuAuth;

import java.util.List;
import java.util.Map;

public interface MenuAuthService {

    public List<Map<String, Object>> select(Map<String, Object> param);

    public int countAll();

    public int insert(Map<String, Object> saveMap);

    public int update(Map<String, Object> saveMap);

}