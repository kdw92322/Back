package com.example.back.service.acc;

import java.util.Map;

public interface AdminDashboardService {

    public Map<String, Object> getExecDeptAmount(Map<String, Object> param);

    public Map<String, Object> getDeptBudgetInfo(Map<String, Object> param);

    public Map<String, Object> getExecTotal(Map<String, Object> param);

    public Map<String, Object> getBudgetExecInfo(Map<String, Object> param);
}
