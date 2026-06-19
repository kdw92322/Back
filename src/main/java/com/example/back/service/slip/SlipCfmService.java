package com.example.back.service.slip;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.back.mapper.slip.SlipCfmMapper;

@Service
public class SlipCfmService {

    @Autowired
    private SlipCfmMapper slipCfmMapper;

    public List<Map<String, Object>> list(Map<String, Object> param) {
        return slipCfmMapper.list(param);
    }

    public List<Map<String, Object>> details(Map<String, Object> param) {
        return slipCfmMapper.details(param);
    }

    public int approve(Map<String, Object> param) {
        return slipCfmMapper.approve(param);
    }

    public int reject(Map<String, Object> param) {
        return slipCfmMapper.reject(param);
    }

    @Transactional
    public int confirm(Map<String, Object> param) {
        slipCfmMapper.confirmCalcBudget(param);
        return slipCfmMapper.confirm(param);
    }

}
