package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.S3.S3Service;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.dto.requests.PostRequest;
import com.unidy.backend.domains.dto.responses.PostResponse;
import com.unidy.backend.domains.entity.*;
import com.unidy.backend.domains.entity.neo4j.PostNode;
import com.unidy.backend.domains.entity.neo4j.UserNode;
import com.unidy.backend.repositories.MySQL_PostRepository;
import com.unidy.backend.repositories.Neo4j_PostRepository;
import com.unidy.backend.repositories.Neo4j_UserRepository;
import com.unidy.backend.services.servicesInterface.PostService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.core.env.Environment;
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
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
    private final Neo4j_PostRepository neo4j_postRepository;
    private final Neo4j_UserRepository userRepository;
    private final S3Service s3Service;
    private final MySQL_PostRepository mySQL_postRepository;
    private final Environment environment;

    public ResponseEntity<?> getPostById(String postID){
        try {
            List<PostResponse> postList = neo4j_postRepository.findPostNodeByPostIdCustom(postID);
            return ResponseEntity.ok().body(postList);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    public ResponseEntity<?> getPostByUserId(Principal connectedUser, int skip, int limit ){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            List<PostResponse> listPost= neo4j_postRepository.findPostNodeByUserId(user.getUserId(), skip, limit);
            return ResponseEntity.ok().body(listPost);
        } catch(Exception e){
            return ResponseEntity.badRequest().body((new ErrorResponseDto(e.toString())));
        }
    }

    public ResponseEntity<?> getPost(Principal connectedUser, int skip, int limit){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            List<PostResponse> listPost= neo4j_postRepository.findPost(user.getUserId(), skip, limit);
            return ResponseEntity.ok().body(listPost);
        } catch(Exception e){
            return ResponseEntity.badRequest().body((new ErrorResponseDto(e.toString())));
        }
    }
    public ResponseEntity<?> createPost(Principal connectedUser, PostRequest request){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        // for mysql storage
        String linkS3 = environment.getProperty("LINK_S3");


        PostNode post = new PostNode();
        UserNode userNode = userRepository.findUserNodeByUserId(user.getUserId());
        JSONArray listImageLink =  new JSONArray();
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

                        String imageUrl = linkS3 + "post-images/" + user.getUserId() + "/" + postImageId + fileContentType;
                        listImageLink.put(imageUrl);
                    } else {
                        return ResponseEntity.badRequest().body(new ErrorResponseDto("Unsupported file format"));
                    }
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
                }
            }
        }

        post.setLinkImage(listImageLink.toString());
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
        String linkS3 = environment.getProperty("LINK_S3");

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
                if (updateRequest.getArrayImageLink() != null) {
                    post.setLinkImage(post.getLinkImage());
                }
                JSONArray listImageLink =  new JSONArray();
                if (updateRequest.getListImageFile() != null ){
                    for (MultipartFile image : updateRequest.getListImageFile()){
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

                                String imageUrl = linkS3 + "post-images/" + user.getUserId() + "/" + postImageId + fileContentType;
                                listImageLink.put(imageUrl);

                            } else {
                                return ResponseEntity.badRequest().body(new ErrorResponseDto("Unsupported file format"));
                            }
                        } catch (Exception e) {
                            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
                        }
                    }
                    post.setLinkImage(post.getLinkImage().replace("]","")+ "," + listImageLink.toString().substring(1) );
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

    public ResponseEntity<?> likePost(Principal connectedUser, String postId){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        UserNode userNode = userRepository.findUserNodeByUserId(user.getUserId());

        try {
            Optional<List<PostNode>> optionalPost = Optional.ofNullable( neo4j_postRepository.findPostNodeByPostId(postId));
            if (optionalPost.isPresent()) {
                PostNode post = optionalPost.get().get(0);
                List<UserNode> usersLike = post.getUserLikes();
                usersLike.add(userNode);
                post.setUserLikes(usersLike);
                neo4j_postRepository.save(post);
                return ResponseEntity.ok().body(new SuccessReponse("Like post success"));
            }
            else {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Can't find this post"));
            }
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }
    public ResponseEntity<?> cancelLikePost(Principal connectedUser, String postId){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        UserNode userNode = userRepository.findUserNodeByUserId(user.getUserId());

        try {
            Optional<List<PostNode>> optionalPost = Optional.ofNullable( neo4j_postRepository.findPostNodeByPostId(postId));
            if (optionalPost.isPresent()) {
                PostNode post = optionalPost.get().get(0);
//                List<UserNode> usersLike = post.getUserLikes();
//                usersLike.remove(userNode);
//                post.setUserLikes(usersLike);
//                neo4j_postRepository.save(post);
                neo4j_postRepository.cancelLikePost(user.getUserId(),postId);
                return ResponseEntity.ok().body(new SuccessReponse("Cancel like post success"));
            }
            else {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Can't find this post"));
            }
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    public CompletableFuture<List<PostNode>> searchPost(String searchTerm, int limit, int skip){
       List<PostNode> posts = neo4j_postRepository.searchPost(searchTerm, limit, skip);
         return CompletableFuture.supplyAsync(() -> posts);
    }
}
