package com.example.back.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetDto {
    private Integer id;           // 내부 PK
    private String budgetCode;    // 과목 코드 (code)
    private String budgetName;    // 과목명 (name)
    private String parentCode;     // 상위 과목 ID
    private String budgetLevel;   // 예산 레벨
    private int amount;          // 예산 금액 (amount)
    private String useYn;         // 사용 여부 (is_use)
    private String remarks;       // 설명 (description)
}