package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.PostResponse;
import com.unidy.backend.domains.entity.neo4j.PostNode;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface Neo4j_PostRepository extends Neo4jRepository<PostNode,String> {

    List<PostNode> findPostNodeByPostId(String postId);

    @Query("""
        OPTIONAL MATCH (user:user {user_id: $userId})-[r:LIKE]->(post:post {post_id: $postId})
        RETURN CASE WHEN r IS NOT NULL THEN true ELSE false END
    """)
    boolean isLikedPost(int userId, String postId);

    @Query("""
            MATCH (user:user)-[r:HAS_POST]->(post:post {post_id: $postId})
            OPTIONAL MATCH (user)-[isLiked:LIKE]->(post)
            OPTIONAL MATCH (:user)-[r_like:LIKE]->(post)
            OPTIONAL MATCH (post) - [has_comment:HAS_COMMENT] -> (comment: comment)
            WITH post,user, r, count(r_like) AS likeCount, r_like, isLiked, has_comment
            RETURN post,user as userNodes, r, likeCount, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked, count(has_comment) as numberComments
            """)
    List<PostResponse> findPostNodeByPostIdCustom(int userId, String postId);

    @Query("""
            MATCH (user:user {user_id: $userId})-[r:HAS_POST]->(post:post)
            OPTIONAL MATCH (user)-[isLiked:LIKE]->(post)
            OPTIONAL MATCH (:user)-[r_like:LIKE]->(post)
            OPTIONAL MATCH (post) - [has_comment:HAS_COMMENT] -> (comment: comment)
            WITH post,user, r, count(r_like) AS likeCount, r_like, isLiked, has_comment
            RETURN post,user as userNodes, r, likeCount, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked, count(has_comment) as numberComments
            ORDER BY post.create_date DESC, post.id ASC
            SKIP $skip
            LIMIT $limit;
            """)
    List<PostResponse> findPostNodeByUserId(int userId, int skip, int limit);

    @Query("""
           MATCH (user:user {user_id: $userId})-[is_friend_1:FRIEND]->(friend:user)-[has_post_1:HAS_POST]->(post:post)OPTIONAL MATCH (user)-[isLiked:LIKE]->(post)
           OPTIONAL MATCH (:user)-[r_like:LIKE]->(post)
           OPTIONAL MATCH (post) - [has_comment:HAS_COMMENT] -> (comment: comment)
           return post as posts, friend as userNodes, has_post_1 as has_posts, count(r_like) as likeCount, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked, count(has_comment) as numberComments
           ORDER BY post.create_date DESC, post.id ASC
           SKIP $skip
           LIMIT $limit;
            """)
    List<PostResponse> findPost(int userId, int skip, int limit);

    @Query("MATCH (p : user {user_id:$userId})-[r:LIKE]->(post: post {post_id: $postId}) DELETE r ;")
    void cancelLikePost(@Param("userId") int userId, @Param("postId") String postId);

    @Query("""
            CALL db.index.fulltext.queryNodes("searchPostIndex", $searchTerm) YIELD node, score
            WITH node, score
            MATCH (userNodes:user)-[r:HAS_POST]->(post:post)
            WHERE post = node
            OPTIONAL MATCH (:user {user_id: $userId})-[isLiked:LIKE]->(post)
            OPTIONAL MATCH (:user)-[r_like:LIKE]->(post)
            OPTIONAL MATCH (post) - [has_comment:HAS_COMMENT] -> (comment: comment)
            WITH post, userNodes, r, count(r_like) AS likeCount, r_like, isLiked, score
            RETURN post, userNodes, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked, count(has_comment) as numberComments
            ORDER BY score DESC, post.create_date DESC, post.id ASC
            SKIP $skip
            LIMIT $limit;
    """)
    List<PostResponse> searchPost(int userId, String searchTerm, int limit, int skip);

    void deletePostByPostId(String postId);
}
