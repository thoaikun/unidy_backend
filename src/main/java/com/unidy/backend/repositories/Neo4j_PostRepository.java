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
    @Query("MATCH (user:user {user_id: $userId})-[r:HAS_POST]->(post:post) RETURN post,r")
    List<PostNode> findPostNodeByUserId(@Param("userId") int userId);

    @Query("MATCH (user:user {user_id: 2})-[:FRIEND]->(userNodes:user)-[r:HAS_POST]->(post:post)<-[r_like:LIKE]-(userLikes: user)\n" +
            "            RETURN post,userNodes,r,userLikes,count(userLikes) AS likeCount\n" +
            "            ORDER BY post.create_date DESC")
    List<PostResponse> findPost(@Param("userId") int userId);
}
