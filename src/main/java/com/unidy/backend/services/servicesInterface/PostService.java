package com.unidy.backend.services.servicesInterface;

import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface PostService {
    public ResponseEntity<?> getPostById(int postId);
    public ResponseEntity<?> getPostByUserId(int userID);

    ResponseEntity<?> getPost(Principal connectUser);
}
