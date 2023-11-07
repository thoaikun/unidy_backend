package com.unidy.backend.controllers;

import com.unidy.backend.domains.dto.UserDto;
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

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInformation(@RequestParam int userId){
        try{
            return ResponseEntity.ok().body(userService.getUserInformation(userId));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("Lỗi hệ thống");
        }
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateUserInformation(@RequestBody  @Valid UserDto userDto){
        try{
            return userService.updateUserInformation(userDto);
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
