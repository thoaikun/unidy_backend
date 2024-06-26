package com.unidy.backend.controllers;

import com.unidy.backend.domains.Type.CampaignStatus;
import com.unidy.backend.domains.dto.requests.AuthenticationRequest;
import com.unidy.backend.domains.dto.requests.PostCondition;
import com.unidy.backend.domains.dto.requests.RegisterRequest;
import com.unidy.backend.services.servicesInterface.AdminService;
import com.unidy.backend.services.servicesInterface.CampaignService;
import com.unidy.backend.services.servicesInterface.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor

public class AdminController {
    private final AdminService adminService;

    @PostMapping("/authenticate/register")
    public ResponseEntity<?> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        return adminService.register(request);
    }

    @PostMapping("/authenticate/login")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        return adminService.authenticate(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/runOrStopJob")
    public ResponseEntity<?> runOrStopScheduleJob(@RequestParam("jobId") int jobId){
        return adminService.runOrStopJob(jobId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/posts/{postId}/block/{block}")
    public ResponseEntity<?> blockOrUnblockPost(@PathVariable String postId,@PathVariable String block){
        return adminService.blockOrUnblockPost(postId,block);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable String postId){
        return adminService.deletePost(postId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/posts")
    public ResponseEntity<?> getPost(@RequestBody PostCondition postCondition) {
        return adminService.getPostByDate(postCondition.getFromDate(), postCondition.getToDate(), postCondition.getPageNumber(), postCondition.getPageSize());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPostByPostId(@PathVariable String postId){
        return adminService.getPostByPostId(postId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/organizations/{organizationId}/approve")
    public ResponseEntity<?> approveOrganization(@PathVariable int organizationId){
        return adminService.approveOrganization(organizationId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/volunteers")
    public ResponseEntity<?> getAllVolunteers(@RequestParam("pageNumber") int pageNumber,
                                              @RequestParam("pageSize") int pageSize){
        return adminService.getAllVolunteers(pageNumber, pageSize);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/volunteers/{volunteerId}")
    public ResponseEntity<?> getVolunteerByVolunteerId(@PathVariable int volunteerId){
        return adminService.getVolunteerByVolunteerId(volunteerId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/organizations")
    public ResponseEntity<?> getAllOrganizations(@RequestParam("pageNumber") int pageNumber,
                                                 @RequestParam("pageSize") int pageSize){
        return adminService.getAllOrganizations(pageNumber, pageSize);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/organizations/{organizationId}")
    public ResponseEntity<?> getOrganizationInformation(@PathVariable int organizationId){
        return adminService.getOrganizationInformation(organizationId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/organizations/unapproved")
    public ResponseEntity<?> getNotApprovedOrganizations(@RequestParam("pageNumber") int pageNumber,
                                                         @RequestParam("pageSize") int pageSize){
        return adminService.getNotApprovedOrganizations(pageNumber, pageSize);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/{userId}/block")
    public ResponseEntity<?> blockOrUnblockUser(@PathVariable int userId){
        return adminService.blockOrUnblockUser(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/campaigns")
    public ResponseEntity<?> getCampaign(@RequestBody PostCondition postCondition){
        return adminService.getCampaign(postCondition);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/campaigns/{campaignId}")
    public ResponseEntity<?> getCampaignByCampaignId(@PathVariable Integer campaignId){
        return adminService.getCampaignByCampaignId(campaignId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/campaigns/{campaignId}/transactions")
    public ResponseEntity<?> getTransactionByCampaignId(@PathVariable int campaignId,
                                                        @RequestParam("pageNumber") int pageNumber,
                                                        @RequestParam("pageSize") int pageSize){
        return adminService.getTransactionByCampaignId(campaignId, pageNumber, pageSize);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/campaigns/{campaignId}/volunteers/approved")
    public ResponseEntity<?> getVolunteersByCampaignId(@PathVariable int campaignId,
                                                       @RequestParam("organizationId") int organizationId,
                                                       @RequestParam("pageNumber") int pageNumber,
                                                       @RequestParam("pageSize") int pageSize){
        return adminService.getApprovedVolunteers(organizationId, campaignId, pageNumber, pageSize);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/campaigns/{campaignId}/volunteers/unapproved")
    public ResponseEntity<?> getVolunteersNotApprovedByCampaignId(@PathVariable int campaignId,
                                                                 @RequestParam("organizationId") int organizationId,
                                                                 @RequestParam("pageNumber") int pageNumber,
                                                                 @RequestParam("pageSize") int pageSize){
        return adminService.getNotApprovedVolunteers(organizationId, campaignId, pageNumber, pageSize);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/settlements/{settlementId}/confirm")
    public ResponseEntity<?> confirmSettlements(@PathVariable int settlementId, Principal userConnected){
        return adminService.confirmSettlements(settlementId,userConnected);
    }

}
