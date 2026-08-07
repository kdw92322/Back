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

import com.example.back.dto.SubjectDto;
import com.example.back.service.acc.AccSubjectService;

@RestController
@RequestMapping("/accsubject")
public class AccSubjectController {

    @Autowired
    private AccSubjectService accSubjectService;

    // 1. 계정 카테고리(과목) 목록 조회 API
    @GetMapping("/list")
    public ResponseEntity<List<SubjectDto>> getBudgetList(@RequestParam Map<String, Object> params) {
        List<SubjectDto> list = accSubjectService.selectAllSubjects(params);
        return ResponseEntity.ok(list);
    }

    // 2. 계정 카테고리 등록 API
    @PostMapping("/insert")
    public ResponseEntity<String> registerBudget(@RequestBody SubjectDto subjectDto) {
        accSubjectService.insertSubject(subjectDto);
        return ResponseEntity.ok("계정 카테고리가 성공적으로 등록되었습니다.");
    }

    // 3. 계정 카테고리 수정 API
    @PostMapping("/update")
    public ResponseEntity<String> updateBudget(@RequestBody SubjectDto subjectDto) {
        int result = accSubjectService.updateSubject(subjectDto);
        if (result > 0) {
            return ResponseEntity.ok("계정 카테고리 정보가 성공적으로 수정되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("계정 정보 수정에 실패했습니다.");
        }
    }

    // 4. 계정 카테고리 삭제 API
    @PostMapping("/delete")
    public ResponseEntity<String> deleteBudget(@RequestBody Map<String, String> params) {
        String budgetCode = params.get("code"); // 테이블 컬럼명에 맞춰 'code'로 수신 권장
        if (budgetCode == null)
            budgetCode = params.get("budget_code");

        if (budgetCode == null) {
            return ResponseEntity.badRequest().body("계정 코드가 누락되었습니다.");
        }

        int result = accSubjectService.deleteSubject(budgetCode);
        if (result > 0) {
            return ResponseEntity.ok("계정 카테고리가 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("계정 카테고리 삭제에 실패했습니다.");
        }
    }
}
