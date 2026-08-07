package com.example.back.controller.acc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.back.service.acc.AdminDashboardService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @GetMapping("/exec-dept-amount")
    public Map<String, Object> getExecDeptAmount(@RequestParam(required = false) Map<String, Object> param) {
        return adminDashboardService.getExecDeptAmount(param);
    }

    @GetMapping("/dept-budget-info")
    public Map<String, Object> getDeptBudgetInfo(Map<String, Object> param) {
        return adminDashboardService.getDeptBudgetInfo(param);
    }

    @GetMapping("/exec-total")
    public Map<String, Object> getExecTotal(@RequestParam(required = false) Map<String, Object> param) {
        return adminDashboardService.getExecTotal(param);
    }

    @GetMapping("/budget-exec-info")
    public Map<String, Object> getBudgetExecInfo(@RequestParam(required = false) Map<String, Object> param) {

        return adminDashboardService.getBudgetExecInfo(param);
    }
}
