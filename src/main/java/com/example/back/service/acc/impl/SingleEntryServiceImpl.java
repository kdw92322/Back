package com.example.back.service.acc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.mapper.acc.ExpenseRptMapper;
import com.example.back.mapper.acc.SingleEntryMapper;
import com.example.back.mapper.slip.SlipMapper;
import com.example.back.service.acc.SingleEntryService;

@Service
public class SingleEntryServiceImpl implements SingleEntryService {

    @Autowired
    private SingleEntryMapper singleEntryMapper;

    @Autowired
    private ExpenseRptMapper expenseRptMapper;

    @Override
    public List<Map<String, Object>> selectList(Map<String, Object> paramMap) {
        return singleEntryMapper.selectList(paramMap);
    }

    @Override
    public int insert(Map<String, Object> saveMap) {
        int rtnval = 0;
        try {
            // 1. Master
            Map<String, Object> mstMap = new HashMap<>();
            mstMap.put("title", "[단식부기]" + saveMap.get("remark"));
            mstMap.put("exp_type", "1");
            mstMap.put("report_date", saveMap.get("report_date"));
            mstMap.put("dept_code", saveMap.get("dept_code"));
            mstMap.put("budget_code", saveMap.get("budget_code"));
            mstMap.put("createBy", saveMap.get("createBy"));
            mstMap.put("status_code", 0);
            mstMap.put("total_amount", saveMap.get("amount"));

            expenseRptMapper.insertExpenseReport(mstMap);

            String no = String.valueOf(mstMap.get("no"));

            // 2. Detail
            Map<String, Object> dtlMap = new HashMap<>();
            dtlMap.put("no", no);
            dtlMap.put("acc_code", saveMap.get("acc_code"));
            dtlMap.put("amount", saveMap.get("amount"));
            dtlMap.put("remark", saveMap.get("remark"));
            expenseRptMapper.insertExpenseReportDetails(dtlMap);

            rtnval = 1;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("==========================");
            System.out.println(e);
            System.out.println("==========================");
            rtnval = 0;
        }
        return rtnval;

    }
}
