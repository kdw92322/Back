package com.example.back.service.acc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.acc.AdminDashboardMapper;

import java.util.Map;

@Service
public class AdminDashboardService {

    @Autowired
    private AdminDashboardMapper adminDashboardMapper;

    public Map<String, Object> getExecDeptAmount(String shYear, String shDeptCode) {
        return adminDashboardMapper.getExecDeptAmount(shYear, shDeptCode);
    }

    public Map<String, Object> getDeptBudgetInfo(String shYear, String shDeptCode) {
        return adminDashboardMapper.getDeptBudgetInfo(shYear, shDeptCode);
    }

    public Map<String, Object> getExecTotal(String shYear, String shDeptCode) {
        return adminDashboardMapper.getExecTotal(shYear, shDeptCode);
    }

    public Map<String, Object> getBudgetExecInfo(String year) {
        return adminDashboardMapper.getBudgetExecInfo(year);
    }
}
