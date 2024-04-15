package com.unidy.backend.controllers;

import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.dto.responses.CampaignPostResponse;
import com.unidy.backend.domains.dto.responses.NodeFulltextSearchResponse;
import com.unidy.backend.domains.dto.responses.PostResponse;
import com.unidy.backend.domains.entity.neo4j.CampaignNode;
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
@RequestMapping("/api/v1/search")
public class SearchController {
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
            CompletableFuture<List<CampaignPostResponse.CampaignPostResponseData>> searchCampaign = campaignService.searchCampaign(connectedUser, searchTerm, limit, skip);
            CompletableFuture<List<PostResponse>> searchPost = postService.searchPost(connectedUser, searchTerm, limit, skip);
            CompletableFuture<List<UserNode>> searchUser = userService.searchUser(connectedUser, searchTerm, limit, skip, "ALL");
            List<Neo4JNode> results = CompletableFuture.allOf(searchCampaign, searchPost, searchUser)
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
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Không thể tìm kiếm dữ liệu."));
        }
    }

    @GetMapping("/campaign")
    public ResponseEntity<?> searchCampaign(
        Principal connectedUser,
        @RequestParam(defaultValue = "") String searchTerm,
        @RequestParam(defaultValue = "5", required = false) int limit,
        @RequestParam(defaultValue = "0", required = false) int skip
    ) {
        try {
            List<CampaignPostResponse.CampaignPostResponseData> campaigns = campaignService.searchCampaign(connectedUser ,searchTerm, limit, skip).join();
            List<Neo4JNode> results = new ArrayList<>(campaigns);
            NodeFulltextSearchResponse response = NodeFulltextSearchResponse.builder()
                    .totals(results.size())
                    .hits(results)
                    .build();
            return ResponseEntity.ok().body(response);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Không thể tìm kiếm dữ liệu."));
        }
    }

    @GetMapping("/post")
    public ResponseEntity<?> searchPost(
        Principal connectUser,
        @RequestParam(defaultValue = "") String searchTerm,
        @RequestParam(defaultValue = "5", required = false) int limit,
        @RequestParam(defaultValue = "0", required = false) int skip
    ){
        try {
            List<PostResponse> posts = postService.searchPost(connectUser, searchTerm, limit, skip).join();
            List<Neo4JNode> results = new ArrayList<>(posts);
            NodeFulltextSearchResponse response = NodeFulltextSearchResponse.builder()
                    .totals(results.size())
                    .hits(results)
                    .build();
            return ResponseEntity.ok().body(response);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Không thể tìm kiếm dữ liệu."));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> searchUser(
        Principal  connectedUser,
        @RequestParam(defaultValue = "") String searchTerm,
        @RequestParam(defaultValue = "5", required = false) int limit,
        @RequestParam(defaultValue = "0", required = false) int skip,
        @RequestParam(defaultValue = "ALL", required = false) String role
    ){
        try {
            List<UserNode> users = userService.searchUser(connectedUser, searchTerm, limit, skip, role).join();
            List<Neo4JNode> results = new ArrayList<>(users);
            NodeFulltextSearchResponse response = NodeFulltextSearchResponse.builder()
                    .totals(results.size())
                    .hits(results)
                    .build();
            return ResponseEntity.ok().body(response);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Không thể tìm kiếm dữ liệu."));
        }
    }
}
