package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.PostResponse;
import com.unidy.backend.domains.entity.neo4j.PostNode;
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
    @Query("MATCH (user:user {user_id: $userId})-[r:HAS_POST]->(post:post)\n" +
            "WHERE post.create_date < $cursor\n" +
            "OPTIONAL MATCH (userLike)-[r_like:LIKE]->(post)\n" +
            "WITH post, r, count(userLike) AS likeCount\n" +
            "RETURN post, r, likeCount\n" +
            "ORDER BY post.create_date DESC\n" +
            "LIMIT $limit;")
    List<PostNode> findPostNodeByUserId(@Param("userId") int userId, @Param("cursor") String cursor, @Param("limit") int limit);

    @Query("MATCH (user:user {user_id: $userId})-[:FRIEND]->(userNodes:user)-[r:HAS_POST]->(post:post)\n" +
            "WHERE post.create_date < $cursor\n" +
            "OPTIONAL MATCH (user)-[isLiked:LIKE]->(post)\n" +
            "OPTIONAL MATCH (userNodes)-[r_like:LIKE]->(post)\n" +
            "WITH post, userNodes, r, count(r_like) AS likeCount, r_like, isLiked\n" +
            "RETURN post, userNodes, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked\n" +
            "ORDER BY post.create_date DESC\n" +
            "LIMIT $limit;")
    List<PostResponse> findPost(@Param("userId") int userId, @Param("cursor") String cursor, @Param("limit") int limit);

    @Query("MATCH (p : user {user_id:$userId})-[r:LIKE]->(post: post {post_id: $postId}) DELETE r ;")
    void cancelLikePost(@Param("userId") int userId, @Param("postId") String postId);


    @Query("""
            CALL db.index.fulltext.queryNodes("searchPostIndex", $searchTerm) YIELD node, score
            WITH node, score
            MATCH (userNodes:user)-[r:HAS_POST]->(post:post)
            WHERE post = node
            OPTIONAL MATCH (user)-[isLiked:LIKE]->(post)
            OPTIONAL MATCH (userNodes)-[r_like:LIKE]->(post)
            WITH post, userNodes, r, count(r_like) AS likeCount, r_like, isLiked, score
            RETURN post, userNodes, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked
            ORDER BY score DESC, post.create_date DESC, post.id ASC
            SKIP $skip
            LIMIT $limit;
    """)
    List<PostNode> searchPost(String searchTerm, int limit, int skip);
}
