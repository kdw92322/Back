package com.example.back.mapper.board;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {
    public List<Map<String, Object>> select(Map<String, Object> param);

    public int insert(Map<String, Object> param);

    public int update(Map<String, Object> param);

    public int delete(Map<String, Object> param);

}
