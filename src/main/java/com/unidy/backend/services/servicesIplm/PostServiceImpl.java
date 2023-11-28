package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.entity.PostNode;
import com.unidy.backend.repositories.MySQL_PostRepository;
import com.unidy.backend.repositories.Neo4j_PostRepository;
import com.unidy.backend.services.servicesInterface.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
    private final Neo4j_PostRepository neo4j_postRepository;
    private final MySQL_PostRepository mySQL_postRepository;
    public ResponseEntity<?> getPostById(int postID){
        try {
            List<PostNode> postList = neo4j_postRepository.findPostNodeByPostId(postID);
            return ResponseEntity.ok().body(postList);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    public ResponseEntity<?> getPostByUserId(int userID){
        try {
            List<PostNode> listPost= neo4j_postRepository.findPostNodeByUserId(userID);
            return ResponseEntity.ok().body(listPost);
        } catch(Exception e){
            return ResponseEntity.badRequest().body((new ErrorResponseDto(e.toString())));
        }
    }
}
