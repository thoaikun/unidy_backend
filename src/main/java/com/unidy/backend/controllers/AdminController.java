package com.unidy.backend.controllers;

import com.unidy.backend.domains.Type.CampaignStatus;
import com.unidy.backend.domains.dto.requests.AuthenticationRequest;
import com.unidy.backend.domains.dto.requests.PostCondition;
import com.unidy.backend.domains.dto.requests.RegisterRequest;
import com.unidy.backend.services.servicesInterface.AdminService;
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
    @GetMapping("/posts/date")
    public ResponseEntity<?> getPost(@RequestBody PostCondition postCondition) {
        return adminService.getPostByDate(postCondition.getFromDate(), postCondition.getToDate(), postCondition.getSkip(), postCondition.getLimit());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/organization/{organizationId}/approve")
    public ResponseEntity<?> approveOrganization(@PathVariable int organizationId){
        return adminService.approveOrganization(organizationId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/{userId}/block")
    public ResponseEntity<?> blockOrUnblockUser(@PathVariable int userId){
        return adminService.blockOrUnblockUser(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/campaigns")
    public ResponseEntity<?> getCampaign(@RequestBody PostCondition postCondition){
        return adminService.getCampaign(postCondition);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/settlements/{settlementId}/confirm")
    public ResponseEntity<?> confirmSettlements(@PathVariable int settlementId, Principal userConnected){
        return adminService.confirmSettlements(settlementId,userConnected);
    }

}
