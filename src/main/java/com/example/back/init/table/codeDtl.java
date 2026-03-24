package com.example.back.init.table;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_common_code_dtl")
public class codeDtl {
    @Id
    @Column(name = "mst_cd", length = 20, nullable = false, columnDefinition = "VARCHAR(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT 'MST 코드'")
    private String mstCd;

    @Id
    @Column(name = "dtl_cd", length = 20, nullable = false, columnDefinition = "VARCHAR(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT 'DTL 코드'")
    private String dtlCd;

    @Column(name = "dtl_nm", length = 20, columnDefinition = "VARCHAR(20) COMMENT 'DTL 이름'")
    private String dtlNm;

    @Column(name = "use_yn", length = 1, columnDefinition = "CHAR(1) COMMENT '사용여부'")
    private String useYn;

    @Column(length = 2000, columnDefinition = "VARCHAR(2000) COMMENT '비고'")
    private String remark;

    @Column(length = 100, columnDefinition = "VARCHAR(100) COMMENT '속성1'")
    private String attr1;

    @Column(length = 100, columnDefinition = "VARCHAR(100) COMMENT '속성2'")
    private String attr2;

    @Column(length = 100, columnDefinition = "VARCHAR(100) COMMENT '속성3'")
    private String attr3;

    @Column(name = "createBy", length = 50, columnDefinition = "VARCHAR(50) COMMENT '작성자'")
    private String createBy;

    @Column(name = "createDt", columnDefinition = "DATETIME COMMENT '작성일시'")
    private LocalDateTime createDt;

    @Column(name = "updateBy", length = 100, columnDefinition = "VARCHAR(100) COMMENT '수정자'")
    private String updateBy;

    @Column(name = "updateDt", columnDefinition = "DATETIME COMMENT '수정일시'")
    private LocalDateTime updateDt;
    
}
