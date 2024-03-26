package com.unidy.backend.services.servicesInterface;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.unidy.backend.domains.dto.requests.CampaignRequest;
import com.unidy.backend.domains.dto.responses.CampaignPostResponse;
import com.unidy.backend.domains.entity.neo4j.CampaignNode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CampaignService {
    ResponseEntity<?> createCampaign(Principal userConnected, CampaignRequest request) throws JsonProcessingException;
    ResponseEntity<?> registerCampaign(Principal userConnected, int campaignId);

    @Async("threadPoolTaskExecutor")
    CompletableFuture<List<CampaignPostResponse.CampaignPostResponseData>> getRecommendationFromKNearest(Principal connectedUser, int offset, int limit) throws Exception;

    @Async("threadPoolTaskExecutor")
    CompletableFuture<List<CampaignPostResponse.CampaignPostResponseData>> getRecommendationFromNeo4J(Principal connectedUser, int skip, int limit) throws Exception;

    List<CampaignPostResponse.CampaignPostResponseData> getCampaignByOrganizationID(int organizationId, int skip, int limit);

    @Async("threadPoolTaskExecutor")
    CompletableFuture<List<CampaignPostResponse.CampaignPostResponseData>> searchCampaign(String searchTerm, int limit, int skip);

    ResponseEntity<?> likeCampaign(Principal connectedUser, String campaignId);

    ResponseEntity<?> cancelLikeCampaign(Principal connectedUser, String campaignId);

    ResponseEntity<?> comment(Principal connectedUser, String postId, String content);

    ResponseEntity<?> getComment(Principal connectedUser, String postId, int skip, int limit);

}
