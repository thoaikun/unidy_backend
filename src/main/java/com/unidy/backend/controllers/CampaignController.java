package com.unidy.backend.controllers;

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
//@PreAuthorize("hasRole('ORGANIZATION')")
@RequiredArgsConstructor

@RequestMapping("/api/v1/campaign")

public class CampaignController {
    private final CampaignService campaignService;
    @PostMapping("")
    public ResponseEntity<?> createCampaign(Principal connectedUser, @ModelAttribute CampaignRequest request){
        return campaignService.createCampaign(connectedUser,request);
    }

    @GetMapping("/register")
    public ResponseEntity<?> registerCampaign(Principal connectedUser, @RequestParam int campaignId){
        return campaignService.registerCampaign(connectedUser, campaignId);
    }

    @GetMapping("/getRecommendCampaign")
    public ResponseEntity<?> registerCampaign(Principal connectedUser){
        return campaignService.getRecommend(connectedUser);
    }
}