package com.unidy.backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.unidy.backend.domains.dto.requests.CampaignRequest;
import com.unidy.backend.domains.dto.requests.PostRequest;
import com.unidy.backend.services.servicesInterface.CampaignService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/v1/campaign")

public class CampaignController {
    private final CampaignService campaignService;

    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping("")
    public ResponseEntity<?> createCampaign(Principal connectedUser, @ModelAttribute CampaignRequest request) throws JsonProcessingException {
        return campaignService.createCampaign(connectedUser,request);
    }

    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/register")
    public ResponseEntity<?> registerCampaign(Principal connectedUser, @RequestParam int campaignId){
        return campaignService.registerCampaign(connectedUser, campaignId);
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    @GetMapping("/getRecommendCampaign")
    public ResponseEntity<?> registerCampaign(Principal connectedUser,@RequestParam int offset, @RequestParam int limit){
        return campaignService.getRecommend(connectedUser, offset, limit);
    }

    @GetMapping("/getCampaignPost")
    public ResponseEntity<?> getCampaignPost(Principal connectedUser,@RequestParam String cursor,@RequestParam int limit){
        return campaignService.getCampaignPost(connectedUser, cursor, limit);
    }
}