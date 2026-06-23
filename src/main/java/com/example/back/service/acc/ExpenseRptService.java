package com.example.back.service.acc;

import java.util.List;
import java.util.Map;

public interface ExpenseRptService {

    // 1. 지출결의서 목록 조회
    public List<java.util.Map<String, Object>> selectAllExpenseReports(java.util.Map<String, Object> params);

    // 2. 지출결의서 상세내역 조회
    public List<Map<String, Object>> getExpenseReportDetailsList(Map<String, Object> params);

    // 3. 지출결의서 등록
    public void insertExpenseReport(Map<String, Object> params);

    // 4. 지출결의서 수정
    public int updateExpenseReport(Map<String, Object> params);

    // 5. 지출결의서 삭제
    public int deleteExpenseReport(String reportId);

    public void insertExpenseReportDetail(List<Map<String, Object>> saveList);

    public int updateExpenseReportDetail(List<Map<String, Object>> saveList);
}
