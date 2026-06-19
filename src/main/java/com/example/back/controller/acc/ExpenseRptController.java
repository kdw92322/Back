package com.example.back.controller.acc;

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

import com.example.back.service.acc.ExpenseRptService;

@RestController
@RequestMapping("/expenserpt")
public class ExpenseRptController {
    
    @Autowired
    private ExpenseRptService expenseRptService;

    // 1. 지출결의서 목록 조회 API
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getExpenseReportList(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> list = expenseRptService.selectAllExpenseReports(params);
        return ResponseEntity.ok(list);
    }

    // 2. 지출결의서 상세내역 조회 API
    @GetMapping("/details")
    public ResponseEntity<List<Map<String, Object>>> getExpenseReportDetailsList(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> list = expenseRptService.getExpenseReportDetailsList(params);
        return ResponseEntity.ok(list);
    }

    // 2. 지출결의서 등록 API
    @PostMapping("/insert")
    public ResponseEntity<String> registerExpenseReport(@RequestBody Map<String, Object> params) {
        //System.out.println("Received params for insert: " + params); // 디버깅용 로그
        expenseRptService.insertExpenseReport(params);

        return ResponseEntity.ok("지출결의서가 성공적으로 등록되었습니다.");
    }

    // 2. 지출결의서-상세 등록 API
    @PostMapping("/insert-detail")
    public ResponseEntity<String> registerExpenseReportDetail(@RequestBody List<Map<String, Object>> saveList) {
        System.out.println("Received params for insert: " + saveList); // 디버깅용 로그
        expenseRptService.insertExpenseReportDetail(saveList);

        return ResponseEntity.ok("지출결의서가 성공적으로 등록되었습니다.");
    }

    // 3. 지출결의서 수정 API
    @PostMapping("/update")
    public ResponseEntity<String> updateExpenseReport(@RequestBody Map<String, Object> params) {
        int result = expenseRptService.updateExpenseReport(params);
        if (result > 0) {
            return ResponseEntity.ok("지출결의서가 성공적으로 수정되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("수정된 사항이 없습니다.");
        }
    }

    // 3. 지출결의서-상세 수정 API
    @PostMapping("/update-detail")
    public ResponseEntity<String> updateExpenseReportDetail(@RequestBody List<Map<String, Object>> saveList) {
        System.out.println("Received params for insert: " + saveList); // 디버깅용 로그
        int result = expenseRptService.updateExpenseReportDetail(saveList);
        System.out.println("Update detail result: " + result); // 디버깅용 로그
        if (result > 0) {
            return ResponseEntity.ok("지출결의서가 성공적으로 수정되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("수정된 사항이 없습니다.");
        }
    }

    // 4. 지출결의서 삭제 API
    @PostMapping("/delete")
    public ResponseEntity<String> deleteExpenseReport(@RequestBody Map<String, Object> params) {
        Object reportId = params.get("report_id");
        if (reportId == null) {
            return ResponseEntity.badRequest().body("결의서 ID가 누락되었습니다.");
        }
        
        int result = expenseRptService.deleteExpenseReport(reportId.toString());
        if (result > 0) {
            return ResponseEntity.ok("지출결의서가 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("지출결의서 삭제에 실패했습니다.");
        }
    }

}