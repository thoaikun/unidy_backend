package com.unidy.backend.controllers;

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

    @GetMapping("/get-post-by-postId")
    public ResponseEntity<?> getPostByPostId(@RequestParam String postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/get-post-by-userId")
    public ResponseEntity<?> getPostByUserId(Principal connectUser, @RequestParam int skip, @RequestParam int limit){
        return postService.getPostByUserId(connectUser, skip, limit);
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

    @PostMapping("/comment")
    public  ResponseEntity<?> commentPost(Principal connectedUser, @RequestParam String postId, @RequestParam String contentComment){
        return postService.comment(connectedUser,postId,contentComment);
    }

    @PostMapping("/reply-comment")
    public  ResponseEntity<?> commentPost(Principal connectedUser, @RequestParam int commentId, @RequestParam String contentReply){
        return postService.replyComment(connectedUser,commentId,contentReply);
    }

    @GetMapping("/comment")
        public  ResponseEntity<?> getComment(Principal connectedUser, @RequestParam String postId, @RequestParam int skip, @RequestParam int limit){
        return postService.getComment(connectedUser,postId,skip,limit);
    }

    @GetMapping("/reply-comment")
    public  ResponseEntity<?> getReplyComment(Principal connectedUser, @RequestParam int commentId, @RequestParam int skip, @RequestParam int limit ){
        return postService.getReplyComment(connectedUser,commentId,skip,limit);
    }
}
