package com.unidy.backend.controllers;

import com.unidy.backend.domains.dto.notification.NotificationDto;
import com.unidy.backend.domains.dto.requests.ApproveVolunteerRequest;
import com.unidy.backend.domains.dto.requests.CampaignDto;
import com.unidy.backend.domains.entity.User;
import com.unidy.backend.services.servicesInterface.OrganizationService;
import com.unidy.backend.services.servicesIplm.CertificateServiceImpl;
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
    private final CertificateServiceImpl certificateService;

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal connectedUser) {
        return organizationService.getProfileOrganization(connectedUser);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/campaigns")
    public ResponseEntity<?> getListCampaign(Principal connectedUser, @RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return organizationService.getListCampaign(user.getUserId(), pageNumber, pageSize);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/campaigns/{campaignId}/not-approved-volunteers")
    public ResponseEntity<?> getListVolunteer(Principal connectedUser, @PathVariable("campaignId") int campaignId, @RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return organizationService.getListVolunteerNotApproved(user.getUserId(), campaignId, pageNumber, pageSize);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PatchMapping("/campaigns/{campaignId}/approve-volunteer")
    public ResponseEntity<?> approveVolunteer(Principal connectedUser, @PathVariable("campaignId") int campaignId, @RequestBody ApproveVolunteerRequest body) {
        return organizationService.approveVolunteer(connectedUser, campaignId, body.getVolunteerIds());
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PatchMapping("/campaigns/{campaignId}/reject-volunteer")
    public ResponseEntity<?> rejectVolunteer(Principal connectedUser, @PathVariable("campaignId") int campaignId, @RequestBody ApproveVolunteerRequest body) {
        return organizationService.rejectVolunteer(connectedUser, campaignId, body.getVolunteerIds());
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/campaigns/{campaignId}/approved-volunteers")
    public ResponseEntity<?> getListVolunteerApproved(
        Principal connectedUser,
        @PathVariable("campaignId") int campaignId,
        @RequestParam("pageNumber") int pageNumber,
        @RequestParam("pageSize") int pageSize
    ) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return organizationService.getListVolunteerApproved(user.getUserId(), campaignId, pageNumber, pageSize);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping("campaigns/{campaignId}/create-certificate")
    public ResponseEntity<?> createCertificate(Principal connectedUser, @PathVariable int campaignId){
        return certificateService.createCertificate(connectedUser,campaignId);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/campaigns/{campaignId}/transactions")
    public ResponseEntity<?> getListCampaignTransaction(Principal connectedUser, @PathVariable("campaignId") int campaignId,
                                                        @RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return organizationService.getListCampaignTransaction(user.getUserId(), campaignId, pageNumber, pageSize);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/transactions")
    public ResponseEntity<?> getListTransaction(
        Principal connectedUser,
        @RequestParam("pageNumber") int pageNumber,
        @RequestParam("pageSize") int pageSize,
        @RequestParam(value = "sort", required = false) String sort
    ) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return organizationService.getListTransaction(user.getUserId(), pageNumber, pageSize, sort);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/notify-member")
    public ResponseEntity<?> getListCampaignTransaction(Principal connectedUser, NotificationDto notificationDto) {
        return organizationService.sendNotifyToMember(connectedUser,notificationDto);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PatchMapping("/campaigns/{campaignId}/end")
    public ResponseEntity<?> endCampaign(Principal connectedUser, @PathVariable int campaignId) {
        return organizationService.endCampaign(connectedUser,campaignId);
    }

    @PatchMapping("/campaigns/{campaignId}")
    public ResponseEntity<?> updateCampaignInformation(Principal connectedUser, @PathVariable int campaignId, @RequestBody CampaignDto campaignDto) {
        return organizationService.updateCampaignInformation(connectedUser,campaignId,campaignDto);
    }

    @PatchMapping("/settlements/{settlementId}/confirm")
    public ResponseEntity<?> confirmSettlements(@PathVariable int settlementId, Principal userConnected){
        return organizationService.confirmSettlements(settlementId,userConnected);
    }
}