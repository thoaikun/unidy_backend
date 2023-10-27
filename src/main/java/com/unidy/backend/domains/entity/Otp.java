package com.unidy.backend.domains.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "otp")
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "otp_id")
    private Integer optId;

    @Column(name = "otp_code")
    private String otpCode;

    @Column(name = "otp_expired")
    private boolean otpExpired;

    @Column(name = "user_id")
    private Integer userId;
}
