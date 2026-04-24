package com.example.back.mapper.s1000d;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface S1000DMapper {
   public List<Map<String, Object>> getDescriptiveDm1(Map<String, Object> param);
   public List<Map<String, Object>> getDescriptiveDm2(Map<String, Object> param);
   public void insertFileInfo(Map<String, Object> fileInfo);
}
