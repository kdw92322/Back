package com.example.back.mapper.acc;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.back.dto.SubjectDto;

@Mapper
public interface AccSubjectMapper {

    int checkSubjectCodeExists(String subjectCode);

    String selectMaxCodeByPrefix(String prefix);

    int insertSubject(SubjectDto subjectDto);

    int updateSubject(SubjectDto subjectDto);

    int deleteSubject(String code);

    List<SubjectDto> selectAllSubjects(Map<String, Object> params);
}