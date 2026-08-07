package com.example.back.mapper.slip;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SlipMapper {
    public String selectNewSlipId(Map<String, Object> params);

    public int insertSlipMaster(Map<String, Object> saveObj);

    public int insertSlipDetail(Map<String, Object> saveObj);
}
