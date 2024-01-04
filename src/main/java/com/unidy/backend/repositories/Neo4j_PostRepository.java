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
    @Query("MATCH (user:user {user_id: $userId})-[r:HAS_POST]->(post:post)\n" +
            "WHERE post.create_date < $cursor\n" +
            "OPTIONAL MATCH (userLike)-[r_like:LIKE]->(post)\n" +
            "WITH post, r, count(userLike) AS likeCount\n" +
            "RETURN post, r, likeCount\n" +
            "ORDER BY post.create_date DESC\n" +
            "LIMIT $limit;")
    List<PostNode> findPostNodeByUserId(@Param("userId") int userId, @Param("cursor") String cursor, @Param("limit") int limit);

    @Query("MATCH (user:user {user_id: 1})-[:FRIEND]->(userNodes:user)-[r:HAS_POST]->(post:post)\n" +
            "WHERE post.create_date < $cursor\n" +
            "OPTIONAL MATCH (userLike)-[r_like:LIKE]->(post)\n" +
            "WITH post, userNodes, r, count(userLike) AS likeCount\n" +
            "RETURN post, userNodes, r, likeCount\n" +
            "ORDER BY post.create_date DESC\n" +
            "LIMIT $limit;")
    List<PostResponse> findPost(@Param("userId") int userId, @Param("cursor") String cursor, @Param("limit") int limit);
}
