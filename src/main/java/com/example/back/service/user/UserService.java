package com.example.back.service.user;

import java.util.List;
import java.util.Map;

public interface UserService {

    public List<Map<String, Object>> selectUserList(Map<String, Object> paramMap);

    public int insert(Map<String, Object> saveMap);

    public int update(Map<String, Object> saveMap);

    public int delete(Map<String, Object> saveMap);
}