package com.example.back.service.acc;

import java.util.List;
import java.util.Map;
import com.example.back.dto.BudgetDto;

public interface AccBudgetService {

    // 예산 코드 중복 확인
    public int checkBudgetCodeExists(String budgetCode);

    // 예산 과목 등록
    public void insertBudget(BudgetDto budgetDto);

    // 예산 과목 수정
    public int updateBudget(BudgetDto budgetDto);

    // 예산 과목 삭제
    public int deleteBudget(String budgetCode);

    // 예산 과목 전체 목록 조회
    public List<BudgetDto> selectAllBudgets(Map<String, Object> params);

    // 예산집행 현황 - 상세 List
    public List<Map<String, Object>> selectExecStat(Map<String, Object> params);

    // 예산집행 현황 - 총 예산 집행 금액
    public Map<String, Object> selectExecTotal(Map<String, Object> params);
}