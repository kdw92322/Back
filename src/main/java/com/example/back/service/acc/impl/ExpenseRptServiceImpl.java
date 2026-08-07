package com.example.back.service.acc.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.acc.ExpenseRptMapper;
import com.example.back.service.acc.ExpenseRptService;
import com.example.back.util.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class ExpenseRptServiceImpl implements ExpenseRptService {
    @Autowired
    private ExpenseRptMapper expenseRptMapper;

    // 1. 지출결의서 목록 조회
    public List<java.util.Map<String, Object>> selectAllExpenseReports(Map<String, Object> params) {
        return expenseRptMapper.selectAllExpenseReports(params);
    }

    // 2. 지출결의서 상세내역 조회
    public List<Map<String, Object>> getExpenseReportDetailsList(Map<String, Object> params) {
        return expenseRptMapper.getExpenseReportDetailsList(params);
    }

    // 3. 지출결의서 등록
    public void insertExpenseReport(Map<String, Object> params) {
        String userId = SecurityUtil.getCurrentUserId();
        ObjectMapper mapper = new ObjectMapper();

        // 3-1. Header
        Map<String, Object> Header = mapper.convertValue(
                params.get("header"), new TypeReference<Map<String, Object>>() {
                });

        Header.put("exp_type", "2");
        Header.put("createBy", userId);
        expenseRptMapper.insertExpenseReport(Header);
    }

    // 4. 지출결의서 수정
    public int updateExpenseReport(Map<String, Object> params) {
        // System.out.println("Received params for update: " + params); // 디버깅용 로그
        ObjectMapper mapper = new ObjectMapper();

        // 4-1. Header
        Map<String, Object> Header = mapper.convertValue(
                params.get("header"), new TypeReference<Map<String, Object>>() {
                });
        System.out.println("Header for update: " + Header); // 디버깅용 로그

        return expenseRptMapper.updateExpenseReport(Header);
    }

    // 5. 지출결의서 삭제
    public int deleteExpenseReport(String reportId) {
        return expenseRptMapper.deleteExpenseReport(reportId);
    }

    public void insertExpenseReportDetail(List<Map<String, Object>> saveList) {
        for (Map<String, Object> saveObj : saveList) {

            // 총 합계금액 Master에 업데이트
            expenseRptMapper.updateTotalAmount(saveObj);
            expenseRptMapper.insertExpenseReportDetails(saveObj);
        }
    }

    public int updateExpenseReportDetail(List<Map<String, Object>> saveList) {
        System.out.println("Received params for updateExpenseReportDetail: " + saveList); // 디버깅용 로그
        int result = 0;
        expenseRptMapper.updateInitTotalAmount(saveList.get(0));

        for (Map<String, Object> saveObj : saveList) {
            expenseRptMapper.updateTotalAmount(saveObj);
            result += expenseRptMapper.updateExpenseReportDetails(saveObj);
        }

        return result;
    }
}
