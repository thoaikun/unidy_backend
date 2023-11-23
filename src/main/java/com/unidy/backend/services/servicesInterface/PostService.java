package com.unidy.backend.services.servicesInterface;

import org.springframework.http.ResponseEntity;

public interface PostService {
    public ResponseEntity<?> getPostById(int postId);
    public ResponseEntity<?> getPostByUserId(int userID);
}
