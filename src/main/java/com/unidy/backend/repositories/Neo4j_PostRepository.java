package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.PostNode;
import jakarta.transaction.Transactional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface Neo4j_PostRepository extends Neo4jRepository<PostNode,Integer> {
    List<PostNode> findPostNodeByPostId(Integer userId);
    @Query("MATCH (user:user {user_id: $userId})-[:HAS_POST]->(post:post) RETURN post")
    List<PostNode> findPostNodeByUserId(@Param("userId") int userId);
}
