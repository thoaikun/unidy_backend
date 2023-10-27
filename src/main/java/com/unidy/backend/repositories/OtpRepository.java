package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Integer> {
    public void deleteByUserId(Integer userId);
    public Optional<Otp> findByUserId(Integer userId);
}
