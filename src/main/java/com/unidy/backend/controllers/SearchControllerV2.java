package com.unidy.backend.controllers;

import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.dto.responses.CampaignPostResponse;
import com.unidy.backend.domains.dto.responses.NodeFulltextSearchResponse;
import com.unidy.backend.domains.dto.responses.NodeFulltextSearchResponseV2;
import com.unidy.backend.domains.entity.neo4j.Neo4JNode;
import com.unidy.backend.domains.entity.neo4j.PostNode;
import com.unidy.backend.domains.entity.neo4j.UserNode;
import com.unidy.backend.domains.role.Role;
import com.unidy.backend.services.servicesInterface.CampaignService;
import com.unidy.backend.services.servicesInterface.PostService;
import com.unidy.backend.services.servicesInterface.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/search")
public class SearchControllerV2 {
    private final CampaignService campaignService;
    private final PostService postService;
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<?> fulltextSearch(
        Principal connectedUser,
        @RequestParam(defaultValue = "") String searchTerm,
        @RequestParam(defaultValue = "5", required = false) int limit,
        @RequestParam(defaultValue = "0", required = false) int skip
    ){
        try {
            CompletableFuture<List<CampaignPostResponse.CampaignPostResponseData>> searchCampaign = campaignService.searchCampaign(searchTerm, limit, skip);
            CompletableFuture<List<PostNode>> searchPost = postService.searchPost(searchTerm, limit, skip);
            CompletableFuture<List<UserNode>> searchUser = userService.searchUser(connectedUser, searchTerm, limit, skip, "ALL");
            NodeFulltextSearchResponseV2.Hits hits = CompletableFuture.allOf(searchCampaign, searchPost, searchUser)
                    .thenApplyAsync(v -> {
                        NodeFulltextSearchResponseV2.Hits temp = new NodeFulltextSearchResponseV2.Hits();
                        temp.setCampaignPostResponseDataHits(searchCampaign.join());
                        temp.setPostNodesHits(searchPost.join());
                        List<UserNode> volunteers = new ArrayList<>();
                        List<UserNode> organizations = new ArrayList<>();
                        for (UserNode userNode : searchUser.join()) {
                            if (userNode.getRole().equals(Role.VOLUNTEER.toString())) {
                                volunteers.add(userNode);
                            } else {
                                organizations.add(userNode);
                            }
                        }
                        temp.setVolunteerNodesHits(volunteers);
                        temp.setOrganizationNodesHits(organizations);
                        return temp;
                    })
                    .join();
            NodeFulltextSearchResponseV2 response = NodeFulltextSearchResponseV2.builder()
                    .totals(hits.getCampaignPostResponseDataHits().size() + hits.getVolunteerNodesHits().size() + hits.getOrganizationNodesHits().size())
                    .hits(hits)
                    .build();
            return ResponseEntity.ok().body(response);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Không thể tìm kiếm dữ liệu."));
        }
    }
}
