package com.example.back.mapper.acc;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminDashboardMapper {
    Map<String, Object> getExecDeptAmount(Map<String, Object> param);

    Map<String, Object> getDeptBudgetInfo(Map<String, Object> param);

    Map<String, Object> getExecTotal(Map<String, Object> param);

    List<Map<String, Object>> getDeptbyExecTotalList(Map<String, Object> param);

    Map<String, Object> getBudgetExecInfo(Map<String, Object> param);
}
