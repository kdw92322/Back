package com.example.back.controller.acc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.BudgetDto;
import com.example.back.service.acc.AccBudgetService;

@RestController
@RequestMapping("/accbudget")
public class AccBudgetController {

    @Autowired
    private AccBudgetService accBudgetService;

    // 1. 예산 과목 목록 조회 API
    @GetMapping("/list")
    public ResponseEntity<List<BudgetDto>> getBudgetList(@RequestParam Map<String, Object> params) {
        List<BudgetDto> list = accBudgetService.selectAllBudgets(params);
        return ResponseEntity.ok(list);
    }

    // 2. 예산 과목 등록 API
    @PostMapping("/insert")
    public ResponseEntity<String> registerBudget(@RequestBody BudgetDto budgetDto) {
        int count = accBudgetService.checkBudgetCodeExists(budgetDto.getBudgetCode());
        if (count > 0) {
            return ResponseEntity.badRequest().body("이미 존재하는 예산 코드입니다.");
        }

        accBudgetService.insertBudget(budgetDto);
        return ResponseEntity.ok("예산 과목이 성공적으로 등록되었습니다.");
    }

    // 3. 예산 과목 수정 API
    @PostMapping("/update")
    public ResponseEntity<String> updateBudget(@RequestBody BudgetDto budgetDto) {
        int result = accBudgetService.updateBudget(budgetDto);
        if (result > 0) {
            return ResponseEntity.ok("예산 정보가 성공적으로 수정되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("예산 정보 수정에 실패했습니다.");
        }
    }

    // 4. 예산 과목 삭제 API
    @PostMapping("/delete")
    public ResponseEntity<String> deleteBudget(@RequestBody Map<String, String> params) {
        String budgetCode = params.get("budget_code");
        if (budgetCode == null) {
            return ResponseEntity.badRequest().body("예산 코드가 누락되었습니다.");
        }

        int result = accBudgetService.deleteBudget(budgetCode);
        if (result > 0) {
            return ResponseEntity.ok("예산 과목이 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("예산 과목 삭제에 실패했습니다.");
        }
    }

    // 1. 예산 집행현황
    @GetMapping("/exec-stat")
    public ResponseEntity<Map<String, Object>> selectExecStat(@RequestParam Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        System.out.println("Received params: " + params);
        List<Map<String, Object>> list = accBudgetService.selectExecStat(params);
        resultMap.put("list", list);

        Map<String, Object> totals = accBudgetService.selectExecTotal(params);
        resultMap.put("totals", totals);

        return ResponseEntity.ok(resultMap);
    }
}