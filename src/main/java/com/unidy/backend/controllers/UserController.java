package com.unidy.backend.controllers;

import com.unidy.backend.domains.dto.requests.ResetPasswordRequest;
import com.unidy.backend.services.servicesInterface.ResetPassword;
import com.unidy.backend.services.servicesIplm.UserServiceIplm;
import com.unidy.backend.domains.dto.requests.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceIplm service;

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
          @RequestBody ChangePasswordRequest request,
          Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }
}
