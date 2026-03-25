package com.example.back.mapper.authMng;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMngMapper {
    public List<Map<String,Object>> selectAuthMngList(Map<String,Object> paramMap);
    int insert(Map<String,Object> saveObj);
    int update(Map<String,Object> saveObj);
    int delete(Map<String,Object> delObj);
    
}

