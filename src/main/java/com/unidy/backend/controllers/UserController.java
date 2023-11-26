package com.unidy.backend.controllers;

import com.unidy.backend.domains.dto.UserDto;
import com.unidy.backend.domains.dto.requests.UserInformationRequest;
import com.unidy.backend.domains.dto.responses.UserInformationRespond;
import com.unidy.backend.domains.entity.User;
import com.unidy.backend.mapper.DtoMapper;
import com.unidy.backend.repositories.UserRepository;
import com.unidy.backend.services.servicesInterface.UserService;
import com.unidy.backend.domains.dto.requests.ChangePasswordRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInformation(Principal connectedUser){
        try{
            return ResponseEntity.ok().body(userService.getUserInformation(connectedUser));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("Lỗi hệ thống");
        }
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateUserInformation(@RequestBody  @Valid UserDto userDto, Principal connectedUser){
        try{
            return userService.updateUserInformation(userDto, connectedUser);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("Lỗi hệ thống");
        }
    }

    @PatchMapping("/new-password")
    public ResponseEntity<?> newPassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        return userService.newPassword(request, connectedUser);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        return userService.changePassword(request, connectedUser);
    }

    @PostMapping("/update-profile-image")
    public  ResponseEntity<?> updateProfileImage(@ModelAttribute UserInformationRequest request){
        return userService.updateProfileImage(request.getProfileImage(), request.getUserId());
    }
}
