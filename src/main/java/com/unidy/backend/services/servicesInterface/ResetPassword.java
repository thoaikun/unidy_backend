package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.requests.OTPRequest;
import com.unidy.backend.domains.dto.requests.ResetPasswordRequest;

public interface ResetPassword {
     String sendOTP(ResetPasswordRequest request);
     boolean submitOTP(OTPRequest OTP);
}
