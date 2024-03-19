package com.unidy.backend.controllers;

import com.unidy.backend.domains.dto.notification.NotificationDto;
import com.unidy.backend.domains.entity.User;
import com.unidy.backend.services.servicesInterface.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/v1/organization")
public class OrganizationController {
    private final OrganizationService organizationService;

    @GetMapping("/get-profile")
    public ResponseEntity<?> getProfile(@RequestParam int organizationId) {
        return organizationService.getProfileOrganization(organizationId);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/list-volunteer-not-approve")
    public ResponseEntity<?> getListVolunteer(Principal connectedUser, @RequestParam("campaignId") int campaignId, @RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return organizationService.getListVolunteerNotApproved(user.getUserId(), campaignId, pageNumber, pageSize);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PatchMapping("/approve-volunteer")
    public ResponseEntity<?> approveVolunteer(Principal connectedUser, @RequestParam("volunteerId") int volunteerId, @RequestParam("campaignId") int campaignId) {
        return organizationService.approveVolunteer(connectedUser, volunteerId, campaignId);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/list-volunteer-approved")
    public ResponseEntity<?> getListVolunteerApproved(Principal connectedUser, @RequestParam("campaignId") int campaignId, @RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return organizationService.getListVolunteerApproved(user.getUserId(), campaignId, pageNumber, pageSize);
    }

    //tat ca cac giao dich
    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/list-transaction")
    public ResponseEntity<?> getListTransaction(Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return organizationService.getListTransaction(user.getUserId());
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/list-campaign-transaction")
    public ResponseEntity<?> getListCampaignTransaction(Principal connectedUser, @RequestParam("campaignId") int campaignId) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return organizationService.getListCampaignTransaction(user.getUserId(), campaignId);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/notify-member")
    public ResponseEntity<?> getListCampaignTransaction(Principal connectedUser, NotificationDto notificationDto) {
        return organizationService.sendNotifyToMember(connectedUser,notificationDto);
    }
}