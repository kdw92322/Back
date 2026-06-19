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
    public Map<String, Object> getExecDeptAmount(
            @RequestParam(required = false) String shYear,
            @RequestParam(required = false) String shDeptCode) {

        return adminDashboardService.getExecDeptAmount(shYear, shDeptCode);
    }

    @GetMapping("/dept-budget-info")
    public Map<String, Object> getDeptBudgetInfo(
            @RequestParam(required = false) String shYear,
            @RequestParam(required = false) String shDeptCode) {

        return adminDashboardService.getDeptBudgetInfo(shYear, shDeptCode);
    }

    @GetMapping("/exec-total")
    public Map<String, Object> getExecTotal(
            @RequestParam(required = false) String shYear,
            @RequestParam(required = false) String shDeptCode) {

        return adminDashboardService.getExecTotal(shYear, shDeptCode);
    }

    @GetMapping("/budget-exec-info")
    public Map<String, Object> getBudgetExecInfo(
            @RequestParam(required = false) String year) {

        return adminDashboardService.getBudgetExecInfo(year);
    }
}
