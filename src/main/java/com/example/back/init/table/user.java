package com.example.back.init.table;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_info")
@Getter
@Setter
public class user {
    @Id
    @Column(name = "id", length = 255, nullable = false, unique = true)
    private String id; // 로그인 아이디 (기존 PK 역할)

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "role", length = 10)
    private String role;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "status", length = 1)
    private String status;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "reg_date")
    private LocalDateTime regDate;
}