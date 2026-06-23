package com.example.back.service.acc;

import java.util.List;
import java.util.Map;

import com.example.back.dto.SubjectDto;

public interface AccSubjectService {

    // 계정 코드(code) 중복 확인
    public int checkSubjectCodeExists(String subjectCode);

    // 계정 카테고리 등록
    public void insertSubject(SubjectDto subjectDto);

    // 계정 카테고리 수정
    public int updateSubject(SubjectDto subjectDto);

    // 계정 카테고리 삭제
    public int deleteSubject(String subjectCode);

    // 계정 카테고리 전체 목록 조회
    public List<SubjectDto> selectAllSubjects(Map<String, Object> params);
}
