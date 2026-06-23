package com.example.back.service.acc;

import java.util.List;
import java.util.Map;
import com.example.back.dto.DeptDto;

public interface AccDeptService {

    public int checkDeptCodeExists(String deptCode);

    public void insertDept(DeptDto deptDto);

    public int updateDept(DeptDto deptDto);

    public int deleteDept(String deptCode);

    public List<DeptDto> selectAllDepts(Map<String, String> params);

    public List<DeptDto> selectComboDepts(Map<String, String> params);

}
