package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.requests.PostRequest;
import com.unidy.backend.domains.dto.responses.PostResponse;
import com.unidy.backend.domains.entity.neo4j.PostNode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PostService {
    ResponseEntity<?> getPostById(Principal connectUser, String postId);
    ResponseEntity<?> getPostByUserId(int userId, int skip, int limit);

    ResponseEntity<?> getPost(Principal connectUser, int skip, int limit);

    ResponseEntity<?> createPost(Principal connectedUser, PostRequest request);

    ResponseEntity<?> updatePost(Principal connectedUser, PostRequest updateRequest);

    ResponseEntity<?> deletePost(Principal connectedUser, String postId);

    ResponseEntity<?> likePost(Principal connectedUser, String postId);

    ResponseEntity<?> cancelLikePost(Principal connectedUser, String postId);

    @Async("threadPoolTaskExecutor")
    CompletableFuture<List<PostResponse>> searchPost(Principal connectUser, String searchTerm, int limit, int skip);

    ResponseEntity<?> comment(Principal connectedUser, String postId, String content);

    ResponseEntity<?> replyComment(Principal connectedUser, Integer commentId, String content);

    ResponseEntity<?> getComment(Principal connectedUser, String postId, int skip, int limit);

    ResponseEntity<?> getReplyComment(Principal connectedUser, Integer commentId, int skip, int limit);
}
