package com.example.back.init.table;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_info")
public class user {
    @Id
    @Column(name = "id", length = 255, nullable = false, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '아이디'")
    private String id;

    @Column(name = "name", length = 255, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '이름'")
    private String name;

    @Column(name = "password", length = 255, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '비밀번호'")
    private String password;

    @Column(name = "role", length = 10, columnDefinition = "VARCHAR(10) COMMENT '권한'")
    private String role;

    @Column(name = "email", length = 100, columnDefinition = "VARCHAR(100) COMMENT '이메일'")
    private String email;

    @Column(name = "status", length = 1, columnDefinition = "CHAR(1) COMMENT '상태'")
    private String status;

    @Column(name = "phone", length = 30, columnDefinition = "VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '전화번호'")
    private String phone;

    @Column(name = "regDate", columnDefinition = "DATETIME COMMENT '가입일자'")
    private LocalDateTime regDate;
}