package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.requests.OTPRequest;
import com.unidy.backend.domains.dto.requests.ResetPasswordRequest;
import org.springframework.http.ResponseEntity;

public interface ResetPassword {
     ResponseEntity<?> sendOTP(ResetPasswordRequest request);
     boolean submitOTP(OTPRequest OTP);
}
