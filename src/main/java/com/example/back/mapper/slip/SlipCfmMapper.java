package com.example.back.mapper.slip;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SlipCfmMapper {
    public List<Map<String, Object>> list(Map<String, Object> param);

    public List<Map<String, Object>> details(Map<String, Object> param);

    public int approve(Map<String, Object> param);

    public int reject(Map<String, Object> param);

    public int confirm(Map<String, Object> param);

    public int confirmCalcBudget(Map<String, Object> param);
}
