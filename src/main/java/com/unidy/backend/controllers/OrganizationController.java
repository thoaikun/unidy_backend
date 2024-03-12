package com.unidy.backend.controllers;

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
    public ResponseEntity<?> getProfile(@RequestParam int organizationId){
       return organizationService.getProfileOrganization(organizationId);
    }

    @GetMapping("/list-volunteer-not-approve")
    public ResponseEntity<?> getListVolunteer(Principal connectedUser){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return organizationService.getListVolunteerNotApproved(user.getUserId());
    }

    @PostMapping("/approve-volunteer")
    public ResponseEntity<?> approveVolunteer(@RequestParam("volunteerId") int volunteerId){
        return organizationService.approveVolunteer(volunteerId);
    }

    @PostMapping("/list-volunteer-approved")
    public ResponseEntity<?> approveVolunteer(Principal connectedUser){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return organizationService.getListVolunteerApproved(user.getUserId());
    }

    //tat ca cac giao dich
    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/list-transaction")
    public ResponseEntity<?> getListTransaction(Principal connectedUser){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return organizationService.getListTransaction(user.getUserId());
    }

    //giao dịch cua chien dich
    @GetMapping("/list-campaign-transaction")
    public ResponseEntity<?> getListCampaignTransaction(Principal connectedUser, @RequestParam("campaignId") int campaignId){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return organizationService.getListCampaignTransaction(user.getUserId(),campaignId);
    }
}
