package com.example.back.mapper.schedule;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScheduleMapper {
    public List<Map<String, Object>> selectScheduleList(Map<String, Object> params);

    public int insert(Map<String, Object> saveMap);

    public int update(Map<String, Object> saveMap);

    public int delete(Map<String, Object> deleteMap);
}
