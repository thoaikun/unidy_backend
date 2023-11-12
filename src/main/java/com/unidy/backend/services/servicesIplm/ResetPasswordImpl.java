package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.dto.requests.EmailDetails;
import com.unidy.backend.domains.dto.requests.OTPRequest;
import com.unidy.backend.domains.dto.requests.ResetPasswordRequest;
import com.unidy.backend.domains.entity.Otp;
import com.unidy.backend.repositories.OtpRepository;
import com.unidy.backend.repositories.UserRepository;
import com.unidy.backend.services.servicesInterface.EmailService;
import com.unidy.backend.services.servicesInterface.ResetPassword;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@Service
@RequiredArgsConstructor
public class ResetPasswordImpl implements ResetPassword {
    private final EmailService emailService;
    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    public ResponseEntity<?> sendOTP(ResetPasswordRequest request) {
        try {
            var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
            if (otpRepository.findByUserId(user.getUserId()).isPresent()) {
                otpRepository.deleteByUserId(user.getUserId());
            }

            String otpCode = generateOTP();
            Otp newOtp = Otp
                    .builder()
                    .otpCode(otpCode)
                    .otpExpired(false)
                    .userId(user.getUserId())
                    .build();
            otpRepository.save(newOtp);
            EmailDetails email = EmailDetails
                    .builder()
                    .recipient(user.getEmail())
                    .msgBody("Your OTP Code :" + otpCode + "\n" + "OTP will be expired in 60 second")
                    .subject("Reset Password OTP")
                    .build();
            emailService.sendTextMail(email);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    otpRepository.deleteByOtpCode(otpCode);
                    timer.cancel();
                }
            }, 60000);
            return ResponseEntity.ok().body(otpCode);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Email không tồn tại"));
        }
    }

    public ResponseEntity<?> submitOTP (OTPRequest OTP){
        try {
            var user = userRepository.findByEmail(OTP.getEmail()).orElseThrow(() -> new Exception("Email không tồn tại"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
        var otp = otpRepository.findByOtpCode(OTP.getOtp());
        if (otp.isPresent()) {
            Otp validateOtp = otp.get();
            otpRepository.deleteByUserId(validateOtp.getUserId());
            return ResponseEntity.ok().body(new SuccessReponse("Success"));
        }
        else {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("OTP không đúng"));
        }
    }

    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
