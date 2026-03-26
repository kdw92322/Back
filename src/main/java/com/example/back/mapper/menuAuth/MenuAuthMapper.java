package com.example.back.mapper.menuAuth;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MenuAuthMapper {
    List<Map<String, Object>> select(Map<String,Object> param);

    int countAll();
    
    int save(Map<String,Object> param);

    int insert(Map<String,Object> param);
}
