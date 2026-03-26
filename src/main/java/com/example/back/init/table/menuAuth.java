package com.example.back.init.table;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_menu_auth")
@Getter
@Setter
public class menuAuth {

    @Id
    @Column(name = "menucode", columnDefinition = "VARCHAR(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '메뉴코드'")
    private String menucode;

    @Id
    @Column(name = "role_id", columnDefinition = "VARCHAR(10) COMMENT '권한'")
    private String roleId;

    @Column(name = "c_yn", columnDefinition = "char(1) DEFAULT NULL COMMENT '저장 Y/N'")
    private String cYn;

    @Column(name = "r_yn", columnDefinition = "char(1) DEFAULT NULL COMMENT '조회 Y/N'")
    private String rYn;

    @Column(name = "d_yn", columnDefinition = "char(1) DEFAULT NULL COMMENT '삭제 Y/N'")
    private String dYn;

    @Column(name = "use_yn", columnDefinition = "varchar(100) DEFAULT NULL COMMENT '사용여부'")
    private String useYn;

    @Column(name = "update_by", columnDefinition = "varchar(100) DEFAULT NULL COMMENT '수정자'")
    private String updateBy;

    @Column(name = "update_dt", columnDefinition = "varchar(100) DEFAULT NULL COMMENT '수정일자'")
    private String updateDt;
}
