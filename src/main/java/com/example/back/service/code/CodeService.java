package com.example.back.service.code;

import java.util.List;
import java.util.Map;

public interface CodeService {

    public List<Map<String, Object>> selectMstCodeList(Map<String, Object> paramMap);

    public List<Map<String, Object>> selectDtlCodeList(Map<String, Object> paramMap);

    public int insert(Map<String, Object> saveObj);

    public int update(Map<String, Object> saveObj);

    public int delete(Map<String, Object> delObj);
}
