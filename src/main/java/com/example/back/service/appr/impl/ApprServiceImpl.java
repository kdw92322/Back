package com.example.back.service.appr.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.acc.ExpenseRptMapper;
import com.example.back.service.appr.ApprService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ApprServiceImpl implements ApprService {

    @Autowired
    private ExpenseRptMapper expenseRptMapper;

    public int submitApproval(Map<String, Object> entity) {
        // System.out.println("Processing approval submission: " + entity);

        System.out.println("Processing approval submission: " + entity.get("opinion"));

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("no", entity.get("key"));
        param.put("status", entity.get("status"));
        param.put("opinion", entity.get("opinion"));
        System.out.println("param approval submission: " + param);

        return expenseRptMapper.updateApprovalStatus(param);
    }

}
