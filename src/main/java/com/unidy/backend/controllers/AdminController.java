package com.unidy.backend.controllers;

import com.unidy.backend.domains.Type.CampaignStatus;
import com.unidy.backend.domains.dto.requests.AuthenticationRequest;
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

    @GetMapping("/runOrStopJob")
    public ResponseEntity<?> runOrStopScheduleJob(@RequestParam("jobId") int jobId){
        return adminService.runOrStopJob(jobId);
    }

    @PatchMapping("/posts/{postId}/block/{block}")
    public ResponseEntity<?> blockOrUnblockPost(@PathVariable String postId,@PathVariable String block){
        return adminService.blockOrUnblockPost(postId,block);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable String postId){
        return adminService.deletePost(postId);
    }

    @GetMapping("/posts/date")
    public ResponseEntity<?> getPostByDate(
            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date toDate,
            @RequestParam("pageNumber") int skip,
            @RequestParam("pageSize") int limit
    ) {
        return adminService.getPostByDate(fromDate, toDate, skip, limit);
    }

    @PatchMapping("/organization/{organizationId}/approve")
    public ResponseEntity<?> approveOrganization(@PathVariable int organizationId){
        return adminService.approveOrganization(organizationId);
    }

    @PatchMapping("/users/{userId}/block")
    public ResponseEntity<?> blockOrUnblockUser(@PathVariable int userId){
        return adminService.blockOrUnblockUser(userId);
    }

    @GetMapping("/campaigns/{status}")
    public ResponseEntity<?> getCampaignByStatus(@PathVariable CampaignStatus status, @RequestParam("skip") int skip,
                                                 @RequestParam("limit") int limit ){
        return adminService.getCampaignByStatus(status,skip,limit);
    }

    @GetMapping("/campaigns/date")
    public ResponseEntity<?> getCampaignPostByDate(
            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date toDate,
            @RequestParam("skip") int skip,
            @RequestParam("limit") int limit
    ) {
        return adminService.getCampaignPostByDate(fromDate, toDate, skip, limit);
    }


    @PatchMapping("/settlements/{settlementId}/confirm")
    public ResponseEntity<?> confirmSettlements(@PathVariable int settlementId, Principal userConnected){
        return adminService.confirmSettlements(settlementId,userConnected);
    }

}
