package com.example.back.service.code;

import java.util.Map;

public interface CodeGrpService {

    public int insert(Map<String, Object> saveObj);

    public int update(Map<String, Object> saveObj);

    public int delete(Map<String, Object> saveObj);
}
