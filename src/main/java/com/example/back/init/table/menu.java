package com.example.back.init.table;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_menu")
@Getter @Setter
public class menu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQL SERIAL 매핑
    @Column(name = "code", length = 10, nullable = false, unique = true)
    private String code;

    @Column(name = "parent_code", length = 10)
    private String parentCode;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "path", length = 20)
    private String path;

    // PostgreSQL에서 order는 예약어이므로 큰따옴표로 감싸거나 매핑 이름을 지정합니다.
    @Column(name = "\"order\"", length = 5)
    private String order;

    @Column(name = "level", length = 1)
    private String level;

    @Column(name = "use_yn", length = 1)
    private String useYn;
    
    @Column(name = "view_path", length = 50)
    private String viewPath;    

    @Column(name = "module", length = 50)
    private String module;    

    @Column(name = "create_by", length = 10)
    private String createBy;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "update_by", length = 10)
    private String updateBy;

    @Column(name = "update_dt")
    private LocalDateTime updateDt;
}
