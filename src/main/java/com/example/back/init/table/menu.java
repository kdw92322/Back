package com.example.back.init.table;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_menu")
public class menu {
    @Id
    @Column(name = "code", length = 10, nullable = false, 
            columnDefinition = "VARCHAR(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '메뉴코드'")
    private String code;

    @Column(name = "parentcode", length = 10, 
            columnDefinition = "VARCHAR(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '상위메뉴코드'")
    private String parentCode;

    @Column(name = "name", length = 100, 
            columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '이름'")
    private String name;

    @Column(name = "path", length = 20, 
            columnDefinition = "VARCHAR(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '경로'")
    private String path;

    @Column(name = "`order`", length = 5, 
            columnDefinition = "VARCHAR(5) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '순서'")
    private String order; // order는 SQL 예약어이므로 백틱(`) 권장

    @Column(name = "level", length = 1, 
            columnDefinition = "CHAR(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '레벨'")
    private String level;

    @Column(name = "use_yn", length = 1, 
            columnDefinition = "CHAR(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '사용여부'")
    private String useYn;
    
    @Column(name = "view_path", length = 50, 
            columnDefinition = "VARCHAR(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT 'view 파일경로'")
    private String view_path;    

    @Column(name = "module", length = 50, 
            columnDefinition = "VARCHAR(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '모듈'")
    private String module;    

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
