package com.example.back.service.acc;

import java.util.List;
import java.util.Map;

public interface SingleEntryService {
    List<Map<String, Object>> selectList(Map<String, Object> paramMap);

    int insert(Map<String, Object> paramMap);
}
