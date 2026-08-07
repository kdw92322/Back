package com.example.back.mapper.acc;

import com.example.back.dto.DeptDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface DeptMapper {
    // 특정 부서코드 조회
    String getCodes(String deptName);

    // 부서 등록
    int insertDept(DeptDto deptDto);

    // 부서 정보 수정
    int updateDept(DeptDto deptDto);

    // 부서 삭제
    int deleteDept(String deptCode);

    // 부서 코드 중복 체크
    int checkDeptCodeExists(String deptCode);

    // 부서조회(조건 포함)
    List<DeptDto> selectAllDepts(Map<String, String> params);

    // 부서조회(ComboBox)
    List<DeptDto> selectComboDepts(Map<String, String> params);

    // 코드로 부서명 조회
    String selectDeptNameById(String deptCode);
}
