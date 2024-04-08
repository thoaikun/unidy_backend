package com.unidy.backend.domains.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "settlement")

public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Integer settlementId;

    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "organization_id")
    private Integer organizationId;

    @Column(name = "admin_confirm")
    private boolean adminConfirm;

    @Column(name = "organization_confirm")

    private boolean organizationConfirm;
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;
}
