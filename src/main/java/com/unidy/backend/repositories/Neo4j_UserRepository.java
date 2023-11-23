package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.UserNode;
import jakarta.transaction.Transactional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface Neo4j_UserRepository extends Neo4jRepository<UserNode,Integer> {
     UserNode findUserNodeByUserId(int userId);
}
