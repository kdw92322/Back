package com.example.back.controller.appr;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.service.appr.ApprService;

@RestController
@RequestMapping("/appr")
public class ApprController {

    private final ApprService apprService;

    ApprController(ApprService apprService) {
        this.apprService = apprService;
    }

    @PostMapping("/submit-approval")
    public Map<String, Object> submitApproval(@RequestBody Map<String, Object> params) {
        params.put("status", '2');
        apprService.submitApproval(params);
        return params;
    }

    @PostMapping("/process")
    public int statusProcess(@RequestBody Map<String, Object> params) {
        return apprService.submitApproval(params);
    }

    @GetMapping("/notifications")
    public List<Map<String, Object>> getNotifications(@RequestParam String userId) {
        return apprService.getNotifications(userId);
    }

    @PostMapping("/line-submit-approval")
    public int lineSubmitApproval(@RequestBody Map<String, Object> params) {
        return apprService.lineSubmitApproval(params);
    }
}
