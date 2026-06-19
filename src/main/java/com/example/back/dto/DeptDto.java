package com.example.back.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeptDto {
    private String deptCode;    // 부서 코드 (PK)
    private String deptName;    // 부서명
    private String description; // 부서 설명 (추가)
    private String parentCode;  // 상위 부서 코드 (추가)
    private Integer sortOrder;  // 정렬 순서
    private String useYn;       // 사용 여부 ('Y' / 'N')
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}