package com.example.back.service.acc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.acc.AdminDashboardMapper;
import com.example.back.service.acc.AdminDashboardService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Autowired
    private AdminDashboardMapper adminDashboardMapper;

    public Map<String, Object> getExecDeptAmount(Map<String, Object> param) {
        return adminDashboardMapper.getExecDeptAmount(param);
    }

    public Map<String, Object> getDeptBudgetInfo(Map<String, Object> param) {
        return adminDashboardMapper.getDeptBudgetInfo(param);
    }

    public Map<String, Object> getExecTotal(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> totals = adminDashboardMapper.getExecTotal(param);
        List<Map<String, Object>> list = adminDashboardMapper.getDeptbyExecTotalList(param);
        result.put("totals", totals);
        result.put("list", list);

        return result;
    }

    public Map<String, Object> getBudgetExecInfo(Map<String, Object> param) {
        return adminDashboardMapper.getBudgetExecInfo(param);
    }
}
