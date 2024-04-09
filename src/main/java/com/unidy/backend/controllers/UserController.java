package com.unidy.backend.controllers;

import com.unidy.backend.domains.dto.UserDto;
import com.unidy.backend.domains.dto.requests.ChoseFavoriteRequest;
import com.unidy.backend.domains.dto.requests.UserInformationRequest;
import com.unidy.backend.services.servicesInterface.UserService;
import com.unidy.backend.domains.dto.requests.ChangePasswordRequest;
import com.unidy.backend.services.servicesIplm.OrganizationServiceIplm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final OrganizationServiceIplm organizationService;

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

    @GetMapping("/profile/volunteers/{volunteerId}")
    public ResponseEntity<?> getUserInformationByUserId(Principal connectedUser, @PathVariable int volunteerId){
        try{
            return userService.getUserInformationByUserId(connectedUser, volunteerId);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("Lỗi hệ thống");
        }
    }

    @GetMapping("/profile/organizations/{organizationId}")
    public ResponseEntity<?> getProfile(Principal connectedUser, @PathVariable int organizationId) {
        return organizationService.getProfileOrganization(connectedUser, organizationId);
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
    public ResponseEntity<?> getListInvite(Principal connectedUser, @RequestParam int skip, @RequestParam int limit){
        return userService.getListInvite(connectedUser, skip, limit);
    }

    @PatchMapping("/decline-invite")
    public ResponseEntity<?> declineInvite(Principal connectedUser, @RequestParam int friendId){
        return userService.declineInvite(connectedUser,friendId);
    }

    @DeleteMapping("/delete-invite")
    public ResponseEntity<?> deleteInvite(Principal connectedUser, @RequestParam int friendId){
        return userService.deleteInvite(connectedUser,friendId);
    }

    @GetMapping("/get-recommend-friend")
    public ResponseEntity<?> getRecommendationFriend(Principal connectedUser,@RequestParam int skip,@RequestParam int limit,@RequestParam int rangeEnd){
        return userService.getRecommendFriend(connectedUser,skip,limit,rangeEnd);
    }

    @GetMapping("/get-list-friend")
    public ResponseEntity<?> getListFriend(Principal connectedUser,@RequestParam int limit, @RequestParam int skip){
        return userService.getListFriend(connectedUser,limit,skip);
    }

    @PostMapping("/choose-favorite-activities")
    public ResponseEntity<?> choseFavoriteActivities(Principal connectedUser, @RequestBody ChoseFavoriteRequest choseFavoriteRequest){
        return userService.choseFavoriteActivities(connectedUser,choseFavoriteRequest);
    }

    @PatchMapping("/follow-organization")
    public ResponseEntity<?> followOrganization(Principal connectedUser, @RequestParam int organizationId){
        return userService.followOrganization(connectedUser,organizationId);
    }

    @PatchMapping("/unfollow-organization")
    public ResponseEntity<?> unFollowOrganization(Principal connectedUser, @RequestParam int organizationId){
        return userService.unFollowOrganization(connectedUser,organizationId);
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getUserTransaction(Principal connectedUser, @RequestParam int pageSize, @RequestParam int pageNumber){
        return userService.getUserTransaction(connectedUser, pageSize, pageNumber);
    }

    @GetMapping("/campaigns")
    public ResponseEntity<?> getUserCampaigns(Principal connectedUser, @RequestParam int pageSize, @RequestParam int pageNumber){
        return userService.getUserJoinedCampaigns(connectedUser, pageSize, pageNumber);
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getUserNotifications(Principal connectedUser, @RequestParam int pageSize, @RequestParam int pageNumber){
        return userService.getNotification(connectedUser, pageSize, pageNumber);
    }

    @PatchMapping("/notifications/unseen")
    public ResponseEntity<?> markAllAsSeen(Principal connectedUser){
        return userService.markAllAsSeen(connectedUser);
    }

    @PatchMapping("/notifications/unseen/{notificationId}")
    public ResponseEntity<?> markAsSeen(Principal connectedUser, @PathVariable int notificationId){
        return userService.markAsSeen(connectedUser, notificationId);
    }

    @GetMapping("/notifications/unseen/count")
    public ResponseEntity<?> getUnseenNotifications(Principal connectedUser){
        return userService.getUnseenNotification(connectedUser);
    }
}
