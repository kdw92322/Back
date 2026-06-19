package com.example.back.mapper.acc;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface AdminDashboardMapper {
    Map<String, Object> getExecDeptAmount(String shYear, String shDeptCode);

    Map<String, Object> getDeptBudgetInfo(String shYear, String shDeptCode);

    Map<String, Object> getExecTotal(String shYear, String shDeptCode);

    Map<String, Object> getBudgetExecInfo(String year);
}
