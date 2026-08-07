package com.example.back.mapper.log;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserLogMapper {

    void insertUserLog(Map<String, Object> insertLogMap);
}
