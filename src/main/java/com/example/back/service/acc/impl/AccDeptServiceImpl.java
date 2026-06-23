package com.example.back.service.acc.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.dto.DeptDto;
import com.example.back.mapper.acc.DeptMapper;
import com.example.back.service.acc.AccDeptService;

@Service
public class AccDeptServiceImpl implements AccDeptService {

    @Autowired
    private DeptMapper deptMapper;

    public int checkDeptCodeExists(String deptCode) {
        return deptMapper.checkDeptCodeExists(deptCode);
    }

    public void insertDept(DeptDto deptDto) {
        deptMapper.insertDept(deptDto);
    }

    public int updateDept(DeptDto deptDto) {
        return deptMapper.updateDept(deptDto);
    }

    public int deleteDept(String deptCode) {
        return deptMapper.deleteDept(deptCode);
    }

    public List<DeptDto> selectAllDepts(Map<String, String> params) {
        return deptMapper.selectAllDepts(params);
    }

    public List<DeptDto> selectComboDepts(Map<String, String> params) {
        return deptMapper.selectComboDepts(params);
    }

}
