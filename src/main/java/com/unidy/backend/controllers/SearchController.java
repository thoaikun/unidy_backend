package com.unidy.backend.controllers;

import com.unidy.backend.domains.dto.responses.NodeFulltextSearchResponse;
import com.unidy.backend.domains.entity.neo4j.CampaignNode;
import com.unidy.backend.domains.entity.neo4j.Neo4JNode;
import com.unidy.backend.domains.entity.neo4j.PostNode;
import com.unidy.backend.domains.entity.neo4j.UserNode;
import com.unidy.backend.services.servicesInterface.CampaignService;
import com.unidy.backend.services.servicesInterface.PostService;
import com.unidy.backend.services.servicesInterface.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {
    private final CampaignService campaignService;
    private final PostService postService;
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<?> fulltextSearch(@RequestParam String searchTerm, @RequestParam int limit, @RequestParam int skip){
        try {
            CompletableFuture<List<CampaignNode>> searchCampaign = campaignService.searchCampaign(searchTerm, limit, skip);
            CompletableFuture<List<PostNode>> searchPost = postService.searchPost(searchTerm, limit, skip);
            CompletableFuture<List<UserNode>> searchUser = userService.searchUser(searchTerm, limit, skip);
            List<Neo4JNode> results = CompletableFuture.allOf(searchCampaign, searchPost)
                    .thenApplyAsync(v -> {
                        List<Neo4JNode> temp = new ArrayList<>();
                        temp.addAll(searchCampaign.join());
                        temp.addAll(searchPost.join());
                        temp.addAll(searchUser.join());
                        return temp;
                    })
                    .join();
            NodeFulltextSearchResponse response = NodeFulltextSearchResponse.builder()
                    .totals(results.size())
                    .hits(results)
                    .build();
            return ResponseEntity.ok().body(response);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.toString());
        }
    }
}
