package com.example.back.mapper.user;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    List<Map<String,Object>> selectUserList(Map<String,Object> paramMap);    
    public int insert(Map<String, Object> saveMap);
    public int update(Map<String, Object> saveMap);
    public int delete(Map<String, Object> saveMap);
}