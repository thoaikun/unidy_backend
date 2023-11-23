package com.unidy.backend.controllers;

import com.unidy.backend.services.servicesInterface.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;

    @GetMapping("/get-post-by-postId")
    public ResponseEntity<?> getPostByPostId(@RequestParam int postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/get-post-by-userId")
    public ResponseEntity<?> getPostByUserId(@RequestParam int userId){
        return postService.getPostByUserId(userId);
    }
}
