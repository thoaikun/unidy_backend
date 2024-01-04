package com.unidy.backend.controllers;

import com.unidy.backend.domains.dto.requests.PostRequest;
import com.unidy.backend.domains.entity.PostNode;
import com.unidy.backend.services.servicesInterface.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @GetMapping("/get-post-by-postId")
    public ResponseEntity<?> getPostByPostId(@RequestParam String postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/get-post-by-userId")
    public ResponseEntity<?> getPostByUserId(Principal connectUser, @RequestParam String cursor, @RequestParam int limit){
        return postService.getPostByUserId(connectUser, cursor, limit);
    }

    @GetMapping("")
    public ResponseEntity<?> getPost(Principal connectUser, @RequestParam String cursor, @RequestParam int limit){
        return postService.getPost(connectUser, cursor, limit);
    }


    @PostMapping("")
    public ResponseEntity<?> createPost(Principal connectedUser, @ModelAttribute PostRequest request){
        return postService.createPost(connectedUser, request);
    }

    @PatchMapping("")
    public  ResponseEntity<?> updatePost(Principal connectedUser, @ModelAttribute PostRequest updateRequest){
        return postService.updatePost(connectedUser, updateRequest);
    }

    @DeleteMapping("")
    public  ResponseEntity<?> updatePost(Principal connectedUser, @RequestParam String postId){
        return postService.deletePost(connectedUser, postId);
    }
}
