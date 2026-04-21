package com.example.back.mapper.s1000d;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface S1000DMapper {
   public void insertFileInfo(String fileName, String filePath, long fileSize);
}
