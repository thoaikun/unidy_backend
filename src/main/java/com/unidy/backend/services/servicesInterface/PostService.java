package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.requests.PostRequest;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface PostService {
    public ResponseEntity<?> getPostById(String postId);
    public ResponseEntity<?> getPostByUserId(int userID);

    ResponseEntity<?> getPost(Principal connectUser);

    ResponseEntity<?> createPost(Principal connectedUser, PostRequest request);

    ResponseEntity<?> updatePost(Principal connectedUser, PostRequest updateRequest);

    ResponseEntity<?> deletePost(Principal connectedUser, String postId);
}