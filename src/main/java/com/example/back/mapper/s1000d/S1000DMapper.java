package com.example.back.mapper.s1000d;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface S1000DMapper {
   public List<Map<String, Object>> selectCsdbList(Map<String, Object> param);
   public void insertCsdbInfo(Map<String, Object> fileInfo);
   
   public List<Map<String, Object>> selectPmc();
   public List<Map<String, Object>> getXmlContentById(Map<String, Object> param);
   public void insertFileInfo(Map<String, Object> fileInfo);
}
