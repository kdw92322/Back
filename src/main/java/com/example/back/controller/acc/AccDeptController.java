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

import com.example.back.dto.DeptDto;
import com.example.back.service.acc.AccDeptService;

@RestController
@RequestMapping("/accdept")
public class AccDeptController  {

    @Autowired
    private AccDeptService accDeptService;

    // 1. 부서 등록 API
    @PostMapping("/insert")
    public ResponseEntity<String> registerDept(@RequestBody DeptDto deptDto) {
    
        if (deptDto.getDeptCode() == null || deptDto.getDeptCode().isEmpty()) {
            return ResponseEntity.badRequest().body("부서 코드가 비어있습니다.");
        }

        int count = accDeptService.checkDeptCodeExists(deptDto.getDeptCode());
        if (count > 0) {
            return ResponseEntity.badRequest().body("이미 존재하는 부서 코드입니다.");
        }
        
        accDeptService.insertDept(deptDto);
        return ResponseEntity.ok("부서가 성공적으로 등록되었습니다.");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateDept(@RequestBody DeptDto deptDto) {
        int result = accDeptService.updateDept(deptDto);
        if (result > 0) {
            return ResponseEntity.ok("부서 정보가 성공적으로 수정되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("부서 정보 수정에 실패했습니다.");
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteDept(@RequestBody Map<String, String> params) {
        System.out.println("Received delete request with params: " + params);
        String deptCode = params.get("dept_code");
        if (deptCode == null) return ResponseEntity.badRequest().body("부서 코드가 누락되었습니다.");
        
        int result = accDeptService.deleteDept(deptCode);
        if (result > 0) {
            return ResponseEntity.ok("부서가 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("부서 삭제에 실패했습니다.");
        }
    }

    // 2. 전체 부서 목록 조회 API
    @GetMapping("/list")
    public ResponseEntity<List<DeptDto>> getDeptList(@RequestParam Map<String, String> params) {
        List<DeptDto> list = accDeptService.selectAllDepts(params);
        return ResponseEntity.ok(list);
    }

    // 3. 전체 부서 목록 조회 API
    @GetMapping("/combo")
    public ResponseEntity<List<DeptDto>> selectComboDepts(@RequestParam Map<String, String> params) {
        List<DeptDto> list = accDeptService.selectComboDepts(params);
        return ResponseEntity.ok(list);
    }
}
