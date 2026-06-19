package com.example.back.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SubjectDto {
    private Integer id;           // 일련번호 (PK)
    private String accCode;          // 계정코드 (Unique)
    private String accName;          // 계정명
    private String budgetCode;    // 예산코드 (상위코드 대용)
    private String drCr;       // 차대구분 (DR/CR)
    private Integer sortOrder;    // 정렬순서
    private String isUse;         // 사용여부 (Y/N)
    private String remark;   // 설명 (비고)
    private String printName;     // 출력명
    private String regId;         // 등록자 ID
    private LocalDateTime regDt;  // 등록일시
    private String modId;         // 수정자 ID
    private LocalDateTime modDt;  // 수정일시
    
    // 검색용 필드
    private String searchKeyword;
}