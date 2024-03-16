package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.PostResponse;
import com.unidy.backend.domains.entity.PostNode;
import jakarta.transaction.Transactional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface Neo4j_PostRepository extends Neo4jRepository<PostNode,String> {

    List<PostNode> findPostNodeByPostId(String postId);

    @Query("MATCH (user:user)-[r:HAS_POST]->(post:post {post_id: $postId})\n" +
            "OPTIONAL MATCH (userLike)-[isLiked:LIKE]->(post)\n" +
            "OPTIONAL MATCH (userNodes)-[r_like:LIKE]->(post)\n" +
            "WITH post,user, r, count(userLike) AS likeCount,r_like, isLiked\n" +
            "RETURN post,user as userNodes, r, likeCount, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked;")
    List<PostResponse> findPostNodeByPostIdCustom(String postId);

    @Query("MATCH (user:user {user_id: $userId})-[r:HAS_POST]->(post:post)\n" +
            "WHERE post.create_date < $cursor\n" +
            "OPTIONAL MATCH (userLike)-[isLiked:LIKE]->(post)\n" +
            "OPTIONAL MATCH (userNodes)-[r_like:LIKE]->(post)\n" +
            "WITH post,user, r, count(userLike) AS likeCount,r_like, isLiked\n" +
            "RETURN post,user as userNodes, r, likeCount, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked\n" +
            "ORDER BY post.create_date DESC\n" +
            "LIMIT $limit;")
    List<PostResponse> findPostNodeByUserId(@Param("userId") int userId, @Param("cursor") String cursor, @Param("limit") int limit);

    @Query("MATCH (user:user {user_id: $userId})-[:FRIEND]->(userNodes:user)-[r:HAS_POST]->(post:post)\n" +
            "WHERE post.create_date < $cursor\n" +
            "OPTIONAL MATCH (user)-[isLiked:LIKE]->(post)\n" +
            "OPTIONAL MATCH (userNodes)-[r_like:LIKE]->(post)\n" +
            "WITH post, user, r, count(r_like) AS likeCount, r_like, isLiked\n" +
            "RETURN post, user as userNodes, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked\n" +
            "ORDER BY post.create_date DESC\n" +
            "LIMIT $limit;")
    List<PostResponse> findPost(@Param("userId") int userId, @Param("cursor") String cursor, @Param("limit") int limit);

    @Query("MATCH (p : user {user_id:$userId})-[r:LIKE]->(post: post {post_id: $postId}) DELETE r ;")
    void cancelLikePost(@Param("userId") int userId, @Param("postId") String postId);
}
