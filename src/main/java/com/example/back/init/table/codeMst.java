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
@Table(name = "tb_common_code_mst")
@Getter @Setter
public class codeMst {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQL SERIAL 매핑
    @Column(name = "mst_cd", length = 20, nullable = false, unique = true)
    private String mstCd;

    @Column(name = "mst_nm", length = 50)
    private String mstNm;

    @Column(name = "use_yn", length = 1)
    private String useYn;

    @Column(length = 2000)
    private String remark;

    @Column(length = 100)
    private String attr1;

    @Column(length = 100)
    private String attr2;

    @Column(length = 100)
    private String attr3;

    @Column(name = "create_by", length = 50)
    private String createBy;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "update_by", length = 50)
    private String updateBy;

    @Column(name = "update_dt")
    private LocalDateTime updateDt;
}
