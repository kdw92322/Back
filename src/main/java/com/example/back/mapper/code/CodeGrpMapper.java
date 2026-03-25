package com.example.back.mapper.code;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CodeGrpMapper {
    public int insert(Map<String, Object> saveObj);
    public int update(Map<String, Object> saveObj);
    public int delete(Map<String, Object> saveObj);
}
