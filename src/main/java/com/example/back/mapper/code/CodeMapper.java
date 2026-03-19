package com.example.back.mapper.code;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CodeMapper {
    List<Map<String,Object>> selectMstCodeList(Map<String,Object> paramMap);
}
