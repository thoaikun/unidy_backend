package com.unidy.backend.services.servicesIplm;

import com.google.gson.Gson;
import com.unidy.backend.S3.S3Service;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.dto.requests.PostRequest;
import com.unidy.backend.domains.dto.responses.CommentResponse;
import com.unidy.backend.domains.dto.responses.InteractResponse;
import com.unidy.backend.domains.dto.responses.PostResponse;
import com.unidy.backend.domains.entity.*;
import com.unidy.backend.domains.entity.neo4j.CommentNode;
import com.unidy.backend.domains.entity.neo4j.PostNode;
import com.unidy.backend.domains.entity.neo4j.UserNode;
import com.unidy.backend.repositories.*;
import com.unidy.backend.services.servicesInterface.PostService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
    private final Neo4j_PostRepository neo4j_postRepository;
    private final Neo4j_UserRepository userRepository;
    private final S3Service s3Service;
    private final PostRepository postRepository;
    private final Environment environment;
    private final Neo4j_UserRepository neo4j_userRepository;
    private final Neo4j_CommentRepository neo4jCommentRepository;
    private final CommentRepository commentRepository;

    public ResponseEntity<?> getPostById(Principal connectUser, String postID){
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectUser).getPrincipal();
            List<PostResponse> postList = neo4j_postRepository.findPostNodeByPostIdCustom(user.getUserId(), postID);
            return ResponseEntity.ok().body(postList);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    public ResponseEntity<?> getPostByUserId(int userId, int skip, int limit ){
        try {
            List<PostResponse> listPost= neo4j_postRepository.findPostNodeByUserId(userId, skip, limit);
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

    @Transactional
    public ResponseEntity<?> createPost(Principal connectedUser, PostRequest request){
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            // for mysql storage
            String linkS3 = environment.getProperty("LINK_S3");
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
                            s3Service.putObject(
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

            CompletableFuture<Integer> savePostToMySQL = savePostToMySQL(request, listImageLink.toString(), user.getUserId());
            CompletableFuture<Integer> savePostToNeo4j = savePostToNeo4j(request, listImageLink.toString(), user.getUserId());

            List<Integer> results = CompletableFuture.allOf(savePostToMySQL, savePostToNeo4j).thenApplyAsync(
                v -> List.of(savePostToMySQL.join(), savePostToNeo4j.join())
            ).join();

            return ResponseEntity.ok().body(new SuccessReponse("Create success"));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Async("threadPoolTaskExecutor")
    protected CompletableFuture<Integer> savePostToMySQL(PostRequest postRequest, String linkImages, Integer userId) {
        try {
            Post post = Post.builder()
                            .postId(LocalDateTime.now().toString() + '_' + userId.toString())
                            .content(postRequest.getContent())
                            .status(postRequest.getStatus())
                            .linkImage(linkImages)
                            .userId(userId)
                            .build();
            postRepository.save(post);
            return CompletableFuture.completedFuture(0);
        } catch (Exception e){
            return CompletableFuture.completedFuture(1);
        }
    }

    @Async("threadPoolTaskExecutor")
    protected CompletableFuture<Integer> savePostToNeo4j(PostRequest postRequest, String linkImages, Integer userId) {
        try {
            PostNode post = new PostNode();
            UserNode userNode = userRepository.findUserNodeByUserId(userId);
            post.setLinkImage(linkImages);
            post.setPostId(LocalDateTime.now().toString()+'_'+userId.toString());
            post.setContent(postRequest.getContent());
            post.setStatus(postRequest.getStatus());
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            post.setCreateDate(sdf.format(date));
            post.setUpdateDate(null);
            post.setIsBlock(false);
            post.setUserNode(userNode);
            neo4j_postRepository.save(post);
            return CompletableFuture.completedFuture(0);
        } catch (Exception e){
            return CompletableFuture.completedFuture(1);
        }
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
                                s3Service.putObject(
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
            if (optionalPost.isPresent() && !optionalPost.get().isEmpty()) {
                boolean isAlreadyLiked = neo4j_postRepository.isLikedPost(user.getUserId(), postId);
                if (isAlreadyLiked){
                    return ResponseEntity.badRequest().body(new ErrorResponseDto("Bạn đã like bài viết này"));
                }

                PostNode post = optionalPost.get().get(0);
                List<UserNode> usersLike = post.getUserLikes();
                usersLike.add(userNode);
                post.setUserLikes(usersLike);
                neo4j_postRepository.save(post);
                return ResponseEntity.ok().body(new InteractResponse("Like bài viết thành công", usersLike.size()));
            }
            else {
                return ResponseEntity.notFound().build();
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
            if (optionalPost.isPresent() && !optionalPost.get().isEmpty()) {
                boolean isAlreadyLiked = neo4j_postRepository.isLikedPost(user.getUserId(), postId);
                if (!isAlreadyLiked){
                    return ResponseEntity.badRequest().body(new ErrorResponseDto("Bạn chưa like bài viết này"));
                }

                PostNode post = optionalPost.get().get(0);
                neo4j_postRepository.cancelLikePost(user.getUserId(),postId);
                List<UserNode> usersLike = post.getUserLikes();
                return ResponseEntity.ok().body(new InteractResponse("Unlike bài viết thành công", usersLike.size() - 1));
            }
            else {
                return ResponseEntity.notFound().build();
            }
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    public CompletableFuture<List<PostResponse>> searchPost(Principal connectUser, String searchTerm, int limit, int skip){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectUser).getPrincipal();
        List<PostResponse> posts = neo4j_postRepository.searchPost(user.getUserId(), searchTerm, limit, skip);
             return CompletableFuture.supplyAsync(() -> posts);
    }

    @Override
    @Transactional
    public ResponseEntity<?> comment(Principal connectedUser, String postId, String content) {
        try {
            if (postId == null) {
                return ResponseEntity.badRequest().body("Post id must not be null");
            }

            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            Comment mysql_comment = Comment.builder()
                    .content(content)
                    .createTime(new Date())
                    .idBlock(false)
                    .replyByComment(null)
                    .build();
            commentRepository.save(mysql_comment);

            List<PostNode> post = neo4j_postRepository.findPostNodeByPostId(postId);
            PostNode commentPost = post.get(0);
            UserNode userComment = neo4j_userRepository.findUserNodeByUserId(user.getUserId());
            CommentNode comment = CommentNode.builder()
                    .commentId(mysql_comment.getCommentId())
                    .body(content)
                    .build();
            comment.setUserComment(userComment);
            comment.setPostNode(commentPost);
            neo4jCommentRepository.save(comment);
            return ResponseEntity.ok().body("Comment success");
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Comment fail");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> replyComment(Principal connectedUser, Integer commentId, String content) {
        try {
            if (commentId == null) {
                return ResponseEntity.badRequest().body("Comment id must not be null");
            }

            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            Comment mysql_comment = Comment.builder()
                    .content(content)
                    .createTime(new Date())
                    .idBlock(false)
                    .replyByComment(null)
                    .build();
            commentRepository.save(mysql_comment);

            CommentNode comment = neo4jCommentRepository.findCommentNodeByCommentId(commentId);
            UserNode userComment = neo4j_userRepository.findUserNodeByUserId(user.getUserId());
            CommentNode reply = CommentNode.builder()
                    .commentId(mysql_comment.getCommentId())
                    .body(content)
                    .build();
            reply.setUserComment(userComment);
            comment.setReplyComment(reply);
            neo4jCommentRepository.save(reply);
            neo4jCommentRepository.save(comment);
            return ResponseEntity.ok().body("Comment success");
        } catch (Exception e){
            return ResponseEntity.ok().body("Comment fail");
        }
    }

    @Override
    public ResponseEntity<?> getComment(Principal connectedUser, String postId, int skip, int limit) {
        try {
            List<CommentResponse> listComment = neo4jCommentRepository.getAllCommentByPostId(postId,skip,limit);
            return ResponseEntity.ok().body(listComment);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @Override
    public ResponseEntity<?> getReplyComment(Principal connectedUser, Integer commentId, int skip, int limit) {
        try {
            List<CommentResponse> listReplyComment = neo4jCommentRepository.getAllReplyComment(commentId, skip, limit);
            return ResponseEntity.ok().body(listReplyComment);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.toString());
        }
    }
}
