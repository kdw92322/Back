package com.example.back.service.slip.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.back.mapper.acc.ExpenseRptMapper;
import com.example.back.mapper.slip.SlipMapper;
import com.example.back.service.slip.SlipService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SlipServiceImpl implements SlipService {

    @Autowired
    private SlipMapper slipMapper;

    @Autowired
    private ExpenseRptMapper expenseRptMapper;

    public List<Map<String, Object>> list(Map<String, Object> param) {
        param.put("slip", "Y");
        return expenseRptMapper.selectAllExpenseReports(param);
    }

    @Transactional
    public int insert(Map<String, Object> saveObj) {
        ObjectMapper mapper = new ObjectMapper();
        int result = 0;

        // 1. header
        Map<String, Object> header = mapper.convertValue(
                saveObj.get("header"),
                new TypeReference<Map<String, Object>>() {
                });
        String slipId = slipMapper.selectNewSlipId(header);
        header.put("slip_id", slipId);
        // System.out.println("header : " + header);

        slipMapper.insertSlipMaster(header);

        // 2. detail
        List<Map<String, Object>> entries = mapper.convertValue(
                saveObj.get("entries"),
                new TypeReference<List<Map<String, Object>>>() {
                });

        for (Map<String, Object> entry : entries) {
            entry.put("slip_id", slipId);
            // System.out.println("entry : " + entry);
            slipMapper.insertSlipDetail(entry);
        }

        return 0;
    }
}
