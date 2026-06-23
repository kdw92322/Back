package com.example.back.service.authMng;

import java.util.List;
import java.util.Map;

public interface AuthMngService {

    public List<Map<String, Object>> selectAuthMngList(Map<String, Object> paramsMap);

    public int insert(Map<String, Object> saveMap);

    public int update(Map<String, Object> saveMap);

    public int delete(Map<String, Object> delMap);

}
