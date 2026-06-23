package com.example.back.service.acc.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.dto.SubjectDto;
import com.example.back.mapper.acc.AccSubjectMapper;
import com.example.back.service.acc.AccSubjectService;
import com.example.back.util.SecurityUtil;

@Service
public class AccSubjectServiceImpl implements AccSubjectService {

    @Autowired
    private AccSubjectMapper accSubjectMapper;

    // 계정 코드(code) 중복 확인
    public int checkSubjectCodeExists(String subjectCode) {
        return accSubjectMapper.checkSubjectCodeExists(subjectCode);
    }

    // 계정 카테고리 등록
    public void insertSubject(SubjectDto subjectDto) {
        // 1. 접두어 설정 (budgetCode가 없으면 'ACC' 기본값, 있으면 최대 17자까지 사용)
        String prefix = subjectDto.getBudgetCode();
        if (prefix == null || prefix.trim().isEmpty()) {
            prefix = "ACC";
        } else if (prefix.length() > 17) {
            prefix = prefix.substring(0, 17);
        }

        // 2. 해당 접두어로 시작하는 최대 코드 조회
        String maxCode = accSubjectMapper.selectMaxCodeByPrefix(prefix);
        int nextSeq = 1;

        if (maxCode != null && maxCode.length() >= 3) {
            try {
                // 마지막 3자리 숫자를 추출하여 +1
                String lastThree = maxCode.substring(maxCode.length() - 3);
                nextSeq = Integer.parseInt(lastThree) + 1;
            } catch (NumberFormatException e) {
                nextSeq = 1;
            }
        }
        String userId = SecurityUtil.getCurrentUserId();
        subjectDto.setRegId(userId);

        // 3. 새로운 코드 생성 (접두어 + 3자리 숫자) 및 DTO 세팅
        subjectDto.setAccCode(prefix + String.format("%03d", nextSeq));
        accSubjectMapper.insertSubject(subjectDto);
    }

    // 계정 카테고리 수정
    public int updateSubject(SubjectDto subjectDto) {
        System.out.println("Updating subject: " + subjectDto);

        String userId = SecurityUtil.getCurrentUserId();
        System.out.println("userId: " + userId);

        subjectDto.setModId(userId);
        return accSubjectMapper.updateSubject(subjectDto);
    }

    // 계정 카테고리 삭제
    public int deleteSubject(String subjectCode) {
        return accSubjectMapper.deleteSubject(subjectCode);
    }

    // 계정 카테고리 전체 목록 조회
    public List<SubjectDto> selectAllSubjects(Map<String, Object> params) {
        return accSubjectMapper.selectAllSubjects(params);
    }
}
