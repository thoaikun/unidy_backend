package com.unidy.backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.dto.requests.CampaignRequest;
import com.unidy.backend.domains.dto.responses.CampaignPostResponse;
import com.unidy.backend.services.servicesInterface.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.devicefarm.model.Run;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    @PreAuthorize("hasRole('VOLUNTEER')")
    @PatchMapping("/register")
    public ResponseEntity<?> registerCampaign(Principal connectedUser, @RequestParam int campaignId){
        return campaignService.registerCampaign(connectedUser, campaignId);
    }

    @PreAuthorize("hasAnyRole('VOLUNTEER')")
    @GetMapping("/recommendation")
    public ResponseEntity<?> getRecommendationCampaign(Principal connectedUser,@RequestParam int offset, @RequestParam int limit) {
        try {
            CompletableFuture<List<CampaignPostResponse.CampaignPostResponseData>> recommendationFromKNearest = campaignService.getRecommendationFromKNearest(connectedUser, offset, limit);
            CompletableFuture<List<CampaignPostResponse.CampaignPostResponseData>> recommendationFromNeo4J = campaignService.getRecommendationFromNeo4J(connectedUser, offset, limit);

            List<CampaignPostResponse.CampaignPostResponseData> result = CompletableFuture.allOf(recommendationFromKNearest, recommendationFromNeo4J)
                    .thenApplyAsync(v -> {
                        List<CampaignPostResponse.CampaignPostResponseData> temp = new ArrayList<>();
                        temp.addAll(recommendationFromKNearest.join());
                        temp.addAll(recommendationFromNeo4J.join());
                        return temp;
                    })
                    .join();
            CampaignPostResponse response = CampaignPostResponse.builder()
                    .campaigns(result)
                    .total(result.size())
                    .nextOffset(offset + limit)
                    .build();
            return ResponseEntity.ok().body(response);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Có lỗi xảy ra khi lấy danh sách chiến dịch gợi ý"));
        }
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<?> getCampaignByOrganizationId(@PathVariable int organizationId, @RequestParam int skip, @RequestParam int limit){
        try {
            List<CampaignPostResponse.CampaignPostResponseData> organizationCampaigns = campaignService.getCampaignByOrganizationID(organizationId, skip, limit);
            CampaignPostResponse response = CampaignPostResponse.builder()
                    .campaigns(organizationCampaigns)
                    .total(organizationCampaigns.size())
                    .nextOffset(skip + limit)
                    .build();
            return ResponseEntity.ok().body(response);
        }
        catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.getMessage()));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Có lỗi xảy ra khi lấy danh sách chiến dịch của tổ chức"));
        }
    }
}