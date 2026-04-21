package com.example.back.init.table;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_menu_auth")
@Getter @Setter
public class menuAuth {

    @Column(name = "menucode", length = 10, nullable = false)
    private String menucode;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQL SERIAL 매핑
    @Column(name = "role_id", length = 10, nullable = false)
    private String roleId;

    @Column(name = "c_yn", length = 1)
    private String cYn;

    @Column(name = "r_yn", length = 1)
    private String rYn;

    @Column(name = "d_yn", length = 1)
    private String dYn;

    @Column(name = "use_yn", length = 100) // 원본 varchar(100) 유지
    private String useYn;

    @Column(name = "update_by", length = 100)
    private String updateBy;

    @Column(name = "update_dt", length = 100) // 원본 varchar(100) 유지
    private String updateDt;
    
    // (선택) 메뉴코드와 권한ID의 쌍이 중복되지 않도록 설정하려면 DB 레벨에서 Unique 제약을 추가합니다.
}
