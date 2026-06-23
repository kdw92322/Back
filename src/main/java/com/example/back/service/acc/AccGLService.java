package com.example.back.service.acc;

import java.util.List;
import java.util.Map;

public interface AccGLService {
    public List<Map<String, Object>> selectGLAccList(Map<String, Object> param);

    public List<Map<String, Object>> selectGLList(Map<String, Object> param);

}
