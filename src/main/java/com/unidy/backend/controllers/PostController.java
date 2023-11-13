package com.unidy.backend.controllers;

import com.unidy.backend.domains.dto.UserDto;
import com.unidy.backend.domains.dto.requests.UserInformationRequest;
import com.unidy.backend.services.servicesInterface.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/get-post-by-userId")
    public ResponseEntity<?> getPostByUserId(UserInformationRequest request) {
        return postService.getPost(request);
    }
}
