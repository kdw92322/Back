package com.example.back.init.table;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_role")
public class role {
    @Id
    @Column(name = "role_id", length = 10, nullable = false, 
            columnDefinition = "VARCHAR(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '권한ID'")
    private String roleId;

    @Column(name = "use_yn", length = 1, 
            columnDefinition = "CHAR(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '사용여부'")
    private String useYn;
    
    @Column(name = "remark", length = 2000, 
            columnDefinition = "VARCHAR(2000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '설명(비고)'")
    private String remark;

    @Column(name = "createBy", length = 10, 
            columnDefinition = "VARCHAR(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '생성자'")
    private String createBy;

    @Column(name = "createDt", columnDefinition = "DATETIME COMMENT '생성일시'")
    private LocalDateTime createDt;

    @Column(name = "updateBy", length = 10, 
            columnDefinition = "VARCHAR(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '수정자'")
    private String updateBy;

    @Column(name = "updateDt", columnDefinition = "DATETIME COMMENT '수정일시'")
    private LocalDateTime updateDt;
}
