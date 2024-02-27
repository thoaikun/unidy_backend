package com.unidy.backend.controllers;

import com.unidy.backend.domains.dto.UserDto;
import com.unidy.backend.domains.dto.requests.ChoseFavoriteRequest;
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
    public ResponseEntity<?> updateProfileImage(@ModelAttribute UserInformationRequest request, Principal connectedUser){
        return userService.updateProfileImage(request.getProfileImage(), connectedUser);
    }

    @PatchMapping("/add-friend")
    public ResponseEntity<?> addFriend(Principal connectedUser, @RequestParam int friendId){
        return userService.addFriend(connectedUser,friendId);
    }


    @PatchMapping("/accept-friend")
    public ResponseEntity<?> acceptRequest(Principal connectedUser, @RequestParam int friendId){
        return userService.acceptFriendInvite(connectedUser,friendId);
    }

    @PatchMapping("/unfriend")
    public ResponseEntity<?> unFriend(Principal connectedUser, @RequestParam int friendId){
        return userService.unfriend(connectedUser,friendId);
    }

    @GetMapping("/list-invite")
    public ResponseEntity<?> getListInvite(Principal connectedUser, @RequestParam String cursor, @RequestParam int limit){
        return userService.getListInvite(connectedUser, cursor, limit);
    }

    @PatchMapping("/delete-invite")
    public ResponseEntity<?> deleteInvite(Principal connectedUser, @RequestParam int friendId){
        return userService.deleteInvite(connectedUser,friendId);
    }

    @GetMapping("/get-recommend-friend")
    public ResponseEntity<?> getRecommendationFriend(Principal connectedUser,@RequestParam int skip,@RequestParam int limit,@RequestParam int rangeEnd){
        return userService.getRecommendFriend(connectedUser,skip,limit,rangeEnd);
    }

    @GetMapping("/get-list-friend")
    public ResponseEntity<?> getListFriend(Principal connectedUser,@RequestParam int limit, @RequestParam int cursor){
        return userService.getListFriend(connectedUser,limit,cursor);
    }

    @PostMapping("/choose-favorite-activities")
    public ResponseEntity<?> choseFavoriteActivities(Principal connectedUser, @RequestBody ChoseFavoriteRequest choseFavoriteRequest){
        return userService.choseFavoriteActivities(connectedUser,choseFavoriteRequest);
    }

    @PatchMapping("/follow-organization")
    public ResponseEntity<?> followOrganization(Principal connectedUser, @RequestParam int organizationId){
        return userService.followOrganization(connectedUser,organizationId);
    }

}
