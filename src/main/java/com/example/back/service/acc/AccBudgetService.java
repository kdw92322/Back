package com.example.back.service.acc;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.dto.BudgetDto;
import com.example.back.mapper.acc.BudgetMapper;

@Service
public class AccBudgetService {
    
    @Autowired
    private BudgetMapper budgetMapper;

    // 예산 코드 중복 확인
    public int checkBudgetCodeExists(String budgetCode) {
        return budgetMapper.checkBudgetCodeExists(budgetCode);
    }

    // 예산 과목 등록
    public void insertBudget(BudgetDto budgetDto) {
        budgetMapper.insertBudget(budgetDto);
    }

    // 예산 과목 수정
    public int updateBudget(BudgetDto budgetDto) {
        return budgetMapper.updateBudget(budgetDto);
    }

    // 예산 과목 삭제
    public int deleteBudget(String budgetCode) {
        return budgetMapper.deleteBudget(budgetCode);
    }

    // 예산 과목 전체 목록 조회
    public List<BudgetDto> selectAllBudgets(Map<String, Object> params) {
        return budgetMapper.selectAllBudgets(params);
    }

    //예산집행 현황 - 상세 List
    public List<Map<String, Object>> selectExecStat(Map<String, Object> params) {
        return budgetMapper.selectExecStat(params);
    }

    //예산집행 현황 - 총 예산 집행 금액
    public Map<String, Object> selectExecTotal(Map<String, Object> params) {
        return budgetMapper.selectExecTotal(params);
    }
}