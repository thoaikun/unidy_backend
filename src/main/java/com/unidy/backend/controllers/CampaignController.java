package com.unidy.backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.unidy.backend.domains.dto.requests.CampaignRequest;
import com.unidy.backend.domains.dto.responses.CampaignPostResponse;
import com.unidy.backend.services.servicesInterface.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/register")
    public ResponseEntity<?> registerCampaign(Principal connectedUser, @RequestParam int campaignId){
        return campaignService.registerCampaign(connectedUser, campaignId);
    }

    @PreAuthorize("hasAnyRole('VOLUNTEER')")
    @GetMapping("/getRecommendCampaign")
    public ResponseEntity<?> registerCampaign(Principal connectedUser,@RequestParam int offset, @RequestParam int limit, @RequestParam String cursor){
        try {
            CompletableFuture<List<CampaignPostResponse.CampaignPostResponseData>> recommendationFromKNearest = campaignService.getRecommendationFromKNearest(connectedUser, offset, limit);
            CompletableFuture<List<CampaignPostResponse.CampaignPostResponseData>> recommendationFromNeo4J = campaignService.getRecommendationFromNeo4J(connectedUser, cursor, limit);

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
                    .nextCursor(recommendationFromNeo4J.get().isEmpty() ? null : recommendationFromNeo4J.get().get(recommendationFromNeo4J.get().size() - 1).getCampaign().getCreateDate())
                    .nextOffset(offset < 100 ? offset + limit : 100)
                    .build();
            return ResponseEntity.ok().body(response);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @GetMapping("/get-organization-campaign")
    public ResponseEntity<?> getCampaignByOrganizationId(@RequestParam int organizationId,@RequestParam String cursor,@RequestParam int limit){
        return campaignService.getCampaignByOrganizationID(organizationId, cursor, limit);
    }
}