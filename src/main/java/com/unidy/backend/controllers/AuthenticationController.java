package com.unidy.backend.controllers;

import com.unidy.backend.domains.dto.requests.AuthenticationRequest;
import com.unidy.backend.domains.dto.requests.OTPRequest;
import com.unidy.backend.domains.dto.requests.ResetPasswordRequest;
import com.unidy.backend.services.servicesInterface.ResetPassword;
import com.unidy.backend.services.servicesIplm.AuthenticationServiceIplm;
import com.unidy.backend.domains.dto.requests.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationServiceIplm service;
  private final ResetPassword resetPassword;

  @PostMapping("/register")
  public ResponseEntity<?> register(
      @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(service.register(request));
  }
  @PostMapping("/authenticate")
  public ResponseEntity<?> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }

  @PostMapping("/send-email-reset-password")
  public ResponseEntity<?> sendOTP (@RequestBody ResetPasswordRequest request) {
    String result = resetPassword.sendOTP(request);
    try {
      if (result.equals("400")){
        return ResponseEntity.badRequest().body("Email not found");
      }
      else{
        return ResponseEntity.ok().body("Send OTP success");
      }
    } catch (Exception e){
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hệ thống xảy ra lỗi");
    }
  }

  @PostMapping("/submit-OPT")
  public ResponseEntity<?> submitOTP(@RequestBody OTPRequest OTP){

    try {
      if (resetPassword.submitOTP(OTP)){
        return ResponseEntity.ok().body("Submit OTP success");
      }
      else{
        return ResponseEntity.badRequest().body("Submit OTP fail");
      }
    } catch (Exception e){
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hệ thống xảy ra lỗi");
    }
  }
}
