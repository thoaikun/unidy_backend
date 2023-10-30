package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.Otp;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

@Transactional
public interface OtpRepository extends JpaRepository<Otp, Integer> {
     @Modifying
     void deleteByUserId(Integer userId);

     @Modifying
     void deleteByOtpCode(String otpCode);
     Optional<Otp> findByUserId(Integer userId);
     Optional<Otp> findByOtpCode(String OTP);
}
