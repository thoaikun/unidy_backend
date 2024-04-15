package com.unidy.backend.controllers;

import com.unidy.backend.domains.dto.requests.CommentRequest;
import com.unidy.backend.domains.dto.requests.PostRequest;
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

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostByPostId(Principal connectUser, @PathVariable String postId) {
        return postService.getPostById(connectUser, postId);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getPostByUserId(@PathVariable int userId, @RequestParam int skip, @RequestParam int limit){
        return postService.getPostByUserId(userId, skip, limit);
    }

    @GetMapping("")
    public ResponseEntity<?> getPost(Principal connectUser, @RequestParam int skip, @RequestParam int limit){
        return postService.getPost(connectUser, skip, limit);
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

    @PatchMapping("/like")
    public  ResponseEntity<?> likePost(Principal connectedUser, @RequestParam String postId){
        return postService.likePost(connectedUser,postId);
    }

    @PatchMapping("/unlike")
    public  ResponseEntity<?> cancelLike(Principal connectedUser, @RequestParam String postId){
        return postService.cancelLikePost(connectedUser,postId);
    }

    @PostMapping("{postId}/comments")
    public  ResponseEntity<?> commentPost(Principal connectedUser, @PathVariable String postId, @RequestBody CommentRequest commentRequest){
        return postService.comment(connectedUser, postId, commentRequest.getContent());
    }

    @PostMapping("{postId}/comments/{commentId}/replies")
    public  ResponseEntity<?> commentPost(Principal connectedUser, @PathVariable int commentId, @RequestBody CommentRequest commentRequest){
        return postService.replyComment(connectedUser,commentId, commentRequest.getContent());
    }

    @GetMapping("{postId}/comments")
    public  ResponseEntity<?> getComment(Principal connectedUser, @PathVariable String postId, @RequestParam int skip, @RequestParam int limit){
        return postService.getComment(connectedUser,postId,skip,limit);
    }

    @GetMapping("{postId}/comments/{commentId}/replies")
    public  ResponseEntity<?> getReplyComment(Principal connectedUser, @PathVariable String postId, @PathVariable Integer commentId, @RequestParam int skip, @RequestParam int limit ){
        return postService.getReplyComment(connectedUser,commentId,skip,limit);
    }
}
