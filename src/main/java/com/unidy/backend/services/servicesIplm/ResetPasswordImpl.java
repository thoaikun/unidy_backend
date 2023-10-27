package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.domains.dto.requests.EmailDetails;
import com.unidy.backend.domains.dto.requests.ResetPasswordRequest;
import com.unidy.backend.domains.entity.Otp;
import com.unidy.backend.domains.entity.User;
import com.unidy.backend.repositories.OtpRepository;
import com.unidy.backend.repositories.UserRepository;
import com.unidy.backend.services.servicesInterface.EmailService;
import com.unidy.backend.services.servicesInterface.ResetPassword;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@Service
@RequiredArgsConstructor
public class ResetPasswordImpl implements ResetPassword {
    private final EmailService emailService;
    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    public String sendOTP(ResetPasswordRequest request) {
        Optional<User> user =  userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            return "400";
        }
        if (otpRepository.findByUserId(user.get().getUserId()).isPresent()){
            otpRepository.deleteById(user.get().getUserId());
        }

        String otpCode = generateOTP();
        Otp newOtp = Otp
                .builder()
                .otpCode(otpCode)
                .otpExpired(false)
                .userId(user.get().getUserId())
                .build();
        otpRepository.save(newOtp);
        EmailDetails email =  EmailDetails
                .builder()
                .recipient(user.get().getEmail())
                .msgBody("Your OTP Code :" + otpCode + "\n" + "OTP will be expired in 60 second")
                .subject("Reset Password OTP")
                .build();
        emailService.sendTextMail(email);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                otpRepository.deleteById(user.get().getUserId());
                timer.cancel();
            }
        }, 60000);
        return otpCode;
    }

    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
