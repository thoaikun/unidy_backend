package com.unidy.backend.domains.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "transaction_time")
    private Date transactionTime;

    @Column(name = "transaction_amount")
    private Long transactionAmount;

    @Column(name = "transaction_code")
    private String transactionCode;

    @Column(name = "signature")
    private String signature;

    @Column(name = "organization_user_id")
    private Integer organizationUserId;

    @Column(name = "campaign_id")
    private Integer campaignId;
}

