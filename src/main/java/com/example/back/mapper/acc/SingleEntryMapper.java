package com.example.back.mapper.acc;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SingleEntryMapper {
    List<Map<String, Object>> selectList(Map<String, Object> paramMap);

    int insert(Map<String, Object> paramMap);

    int update(Map<String, Object> paramMap);

    int delete(Map<String, Object> paramMap);
}
