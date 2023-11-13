package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.dto.UserDto;
import com.unidy.backend.domains.dto.requests.UserInformationRequest;
import com.unidy.backend.domains.entity.Post;
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
    public ResponseEntity<?> getPost(UserInformationRequest request){
        try {
            List<PostNode> postList = neo4j_postRepository.findPostNodeByPostId(1);
            return ResponseEntity.ok().body(postList);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("something error"));
        }
    }
}
