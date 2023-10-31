package com.unidy.backend.controllers;

import com.unidy.backend.domains.dto.requests.ResetPasswordRequest;
import com.unidy.backend.domains.dto.requests.UserInformationRequest;
import com.unidy.backend.services.servicesInterface.ResetPassword;
import com.unidy.backend.services.servicesInterface.UserService;
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

    private final UserService userService;

    @PostMapping("/get-user-information")
    public ResponseEntity<?> getUserInformation(@RequestBody UserInformationRequest request){
        try{
            return ResponseEntity.ok().body(userService.getUserInformation(request));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("Lỗi hệ thống");
        }
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }
}
