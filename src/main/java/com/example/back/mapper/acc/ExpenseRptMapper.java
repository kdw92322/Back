package com.example.back.mapper.acc;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExpenseRptMapper {
    // 1. 지출결의서 목록 조회
    List<Map<String, Object>> selectAllExpenseReports(Map<String, Object> params);

    // 2. 지출결의서 상세내역 조회
    List<Map<String, Object>> getExpenseReportDetailsList(Map<String, Object> params);

    // 3-1. 지출결의서 마스터 등록
    void insertExpenseReport(Map<String, Object> params);

    // 3-2. 지출결의서 디테일 등록
    void insertExpenseReportDetails(Map<String, Object> params);

    // 4-1. 지출결의서 마스터 수정
    int updateExpenseReport(Map<String, Object> params);

    // 4-2. 지출결의서 디테일 수정
    int updateExpenseReportDetails(Map<String, Object> params);

    // 5. 지출결의서 삭제
    int deleteExpenseReport(String reportId);

    //6. 결재 상태 변경
    int updateApprovalStatus(Map<String, Object> params);

    //7. 총금액 업데이트
    int updateTotalAmount(Map<String, Object> params);

    //8. 총금액 초기화
    int updateInitTotalAmount(Map<String, Object> params);

}
