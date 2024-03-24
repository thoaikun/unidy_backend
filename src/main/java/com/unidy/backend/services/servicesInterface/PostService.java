package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.requests.PostRequest;
import com.unidy.backend.domains.entity.neo4j.PostNode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PostService {
    public ResponseEntity<?> getPostById(String postId);
    public ResponseEntity<?> getPostByUserId(Principal connectUser, int skip, int limit);

    ResponseEntity<?> getPost(Principal connectUser, int skip, int limit);

    ResponseEntity<?> createPost(Principal connectedUser, PostRequest request);

    ResponseEntity<?> updatePost(Principal connectedUser, PostRequest updateRequest);

    ResponseEntity<?> deletePost(Principal connectedUser, String postId);

    ResponseEntity<?> likePost(Principal connectedUser, String postId);

    ResponseEntity<?> cancelLikePost(Principal connectedUser, String postId);

    @Async("threadPoolTaskExecutor")
    CompletableFuture<List<PostNode>> searchPost(String searchTerm, int limit, int skip);

    ResponseEntity<?> comment(Principal connectedUser, String postId, String contentComment);

    ResponseEntity<?> replyComment(Principal connectedUser, int commentId, String contentReply);

    ResponseEntity<?> getComment(Principal connectedUser, String postId, int skip, int limit);

    ResponseEntity<?> getReplyComment(Principal connectedUser, int commentId, int skip, int limit);
}
