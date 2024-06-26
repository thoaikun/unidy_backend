package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.config.JwtService;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.TokenType;
import com.unidy.backend.domains.dto.requests.EmailDetails;
import com.unidy.backend.domains.dto.requests.OTPRequest;
import com.unidy.backend.domains.dto.requests.ResetPasswordRequest;
import com.unidy.backend.domains.dto.responses.AuthenticationResponse;
import com.unidy.backend.domains.entity.Otp;
import com.unidy.backend.domains.entity.Token;
import com.unidy.backend.repositories.OtpRepository;
import com.unidy.backend.repositories.TokenRepository;
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
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

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
            }, 180000);
            return ResponseEntity.ok().body(otpCode);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Email không tồn tại"));
        }
    }
    public ResponseEntity<?> submitOTPChangePassword(OTPRequest OTP){
        try {
            var user = userRepository.findByEmail(OTP.getEmail()).orElseThrow(() -> new Exception("Email không tồn tại"));
            var otp = otpRepository.findByOtpCode(OTP.getOtp());
            if (otp.isPresent()) {
                if (!otp.get().getUserId().equals(user.getUserId())){
                    return ResponseEntity.badRequest().body(new ErrorResponseDto("OTP không đúng"));
                }
                Otp validateOtp = otp.get();
                otpRepository.deleteByUserId(validateOtp.getUserId());
                var jwtToken = jwtService.generateToken(user);
                var refreshToken = jwtService.generateRefreshToken(user);
                var token = Token.builder()
                        .user(user)
                        .token(jwtToken)
                        .refreshToken(refreshToken)
                        .tokenType(TokenType.BEARER)
                        .expired(false)
                        .revoked(false)
                        .build();
                tokenRepository.save(token);
                return ResponseEntity.ok().body(AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .build());
            }
            else {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("OTP không đúng"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    };


    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
