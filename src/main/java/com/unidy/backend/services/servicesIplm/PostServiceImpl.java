package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.S3.S3Service;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.dto.requests.PostRequest;
import com.unidy.backend.domains.dto.responses.PostResponse;
import com.unidy.backend.domains.entity.*;
import com.unidy.backend.repositories.MySQL_PostRepository;
import com.unidy.backend.repositories.Neo4j_PostRepository;
import com.unidy.backend.repositories.Neo4j_UserRepository;
import com.unidy.backend.services.servicesInterface.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Optional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
    private final Neo4j_PostRepository neo4j_postRepository;
    private final Neo4j_UserRepository userRepository;
    private final S3Service s3Service;
    private final MySQL_PostRepository mySQL_postRepository;
    public ResponseEntity<?> getPostById(String postID){
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

    public ResponseEntity<?> getPost(Principal connectedUser, String cursor, int limit){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            List<PostResponse> listPost= neo4j_postRepository.findPost(user.getUserId(), cursor, limit);
            return ResponseEntity.ok().body(listPost);
        } catch(Exception e){
            return ResponseEntity.badRequest().body((new ErrorResponseDto(e.toString())));
        }
    }
    public ResponseEntity<?> createPost(Principal connectedUser, PostRequest request){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        // for mysql storage


        PostNode post = new PostNode();
        UserNode userNode = userRepository.findUserNodeByUserId(user.getUserId());
        String listImageLink = "" ;
        if (request.getListImageFile() != null){
            for (MultipartFile image : request.getListImageFile()){
                String postImageId = UUID.randomUUID().toString();
                String fileContentType = image.getContentType();
                try {
                    if (fileContentType != null &&
                            (fileContentType.equals("image/png") ||
                                    fileContentType.equals("image/jpeg") ||
                                    fileContentType.equals("image/jpg"))) {
                        fileContentType = fileContentType.replace("image/",".");
                        s3Service.putImage(
                                "unidy",
                                fileContentType,
                                "post-images/%s/%s".formatted(user.getUserId(), postImageId+fileContentType ),
                                image.getBytes()
                        );

//                UserProfileImage image = userProfileImageRepository.findByUserId(userId);
//                if (image == null){
//                    image = new UserProfileImage();
//                }
//                image.setLinkImage(profileImageId+fileContentType);
//                image.setUpdateDate(new Date());
//                image.setUserId(userId);
//                userProfileImageRepository.save(image);
                        String imageUrl = "/" + user.getUserId() + "/" + postImageId + fileContentType;
                        listImageLink +="{ url: "+ imageUrl +"}"+",";
                    } else {
                        return ResponseEntity.badRequest().body(new ErrorResponseDto("Unsupported file format"));
                    }
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
                }
            }
        }
        
        post.setLinkImage(listImageLink);
        post.setPostId(LocalDateTime.now().toString()+'_'+user.getUserId().toString());
        post.setContent(request.getContent());
        post.setStatus(request.getStatus());

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        post.setCreateDate(sdf.format(date));
        post.setUpdateDate(null);
        post.setIsBlock(false);

        post.setUserNode(userNode);
        // for neo4j storage
        neo4j_postRepository.save(post);
        return ResponseEntity.ok().body(new SuccessReponse("Create success"));
    }

    public ResponseEntity<?> updatePost(Principal connectedUser, PostRequest updateRequest){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (updateRequest.getPostId() == null){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Post id must not be null"));
        }
        try {
            Optional<List<PostNode>> optionalPost = Optional.ofNullable( neo4j_postRepository.findPostNodeByPostId(updateRequest.getPostId()));
            if (optionalPost.isPresent()) {
                PostNode post = optionalPost.get().get(0);
                if (!post.getUserNode().getUserId().equals(user.getUserId())){
                    throw new Exception("You can't update this post");
                }
                if (updateRequest.getContent() != null) {
                    post.setContent(updateRequest.getContent());
                }
                if (updateRequest.getStatus() != null) {
                    post.setStatus(updateRequest.getStatus());
                }
                post.setUpdateDate(String.valueOf(new Date()));
                neo4j_postRepository.save(post);
                return ResponseEntity.ok().body(new SuccessReponse("Update post success"));
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Can't find post"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    public ResponseEntity<?> deletePost(Principal connectedUser, String postId){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            Optional<List<PostNode>> optionalPost = Optional.ofNullable( neo4j_postRepository.findPostNodeByPostId(postId));
            if (optionalPost.isPresent()) {
                PostNode post = optionalPost.get().get(0);
                if (!post.getUserNode().getUserId().equals(user.getUserId())){
                    throw new Exception("You can't delete this post");
                }
                neo4j_postRepository.delete(post);
                return ResponseEntity.ok().body(new SuccessReponse("Delete post success"));
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Can't find post"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }
}
