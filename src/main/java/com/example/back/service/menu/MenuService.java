package com.example.back.service.menu;

import java.util.List;
import java.util.Map;

public interface MenuService {

    public List<Map<String, Object>> selectMenuList(Map<String, Object> paramsMap);

    public int insert(Map<String, Object> saveMap);

    public int update(Map<String, Object> saveMap);

    public int delete(Map<String, Object> delMap);

    public int save(Map<String, Object> saveMap);
}