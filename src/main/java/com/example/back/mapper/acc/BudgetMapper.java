package com.example.back.mapper.acc;

import com.example.back.dto.BudgetDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface BudgetMapper {
    int insertBudget(BudgetDto budgetDto); // 예산 과목 등록

    int updateBudget(BudgetDto budgetDto); // 예산 과목 수정

    int deleteBudget(String budgetCode); // 예산 과목 삭제

    int checkBudgetCodeExists(String budgetCode); // 코드 중복 체크

    List<BudgetDto> selectAllBudgets(Map<String, Object> params); // 예산 목록 조회

    BudgetDto selectBudgetById(Integer id); // ID로 단일 조회

    List<Map<String, Object>> selectExecStat(Map<String, Object> params);

    Map<String, Object> selectExecTotal(Map<String, Object> params);

    String selectBudgetName(String code);
}