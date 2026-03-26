package com.example.back.mapper.menu;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MenuMapper {
    public List<Map<String, Object>> selectMenuList(Map<String, Object> paramsMap);

    public String createNewMenuCode();

    public int insert(Map<String, Object> saveMap);

    public int update(Map<String, Object> saveMap);

    public int delete(Map<String, Object> delMap);

}
