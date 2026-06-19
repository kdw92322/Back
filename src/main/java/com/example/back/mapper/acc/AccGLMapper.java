package com.example.back.mapper.acc;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccGLMapper {
    List<Map<String, Object>> selectGLAccList(Map<String, Object> param);    
    List<Map<String, Object>> selectGLList(Map<String, Object> param);
}
