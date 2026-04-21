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
@Table(name = "tb_role")
@Getter @Setter
public class role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQL SERIAL 매핑    
    @Column(name = "role_id", length = 10, nullable = false, unique = true)
    private String roleId;

    @Column(name = "use_yn", length = 1)
    private String useYn;
    
    @Column(name = "remark", length = 2000)
    private String remark;

    @Column(name = "create_by", length = 10)
    private String createBy;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "update_by", length = 10)
    private String updateBy;

    @Column(name = "update_dt")
    private LocalDateTime updateDt;
}
