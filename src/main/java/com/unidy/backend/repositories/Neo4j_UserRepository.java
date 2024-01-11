package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.CheckResult;
import com.unidy.backend.domains.entity.UserNode;
import jakarta.transaction.Transactional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
@Transactional
public interface Neo4j_UserRepository extends Neo4jRepository<UserNode,Integer> {
     UserNode findUserNodeByUserId(int userId);
     @Query("MATCH (user1:user {user_id: $userId})\n" +
             "MATCH (user2:user {user_id: $friendId})\n" +
             "MERGE (user1)-[:INVITE_FRIEND]->(user2)\n")
     void friendInviteRequest(@RequestParam int userId, @RequestParam int friendId);

     @Query(" OPTIONAL MATCH (user1: user {user_id: $friendId})-[r:INVITE_FRIEND]->(user2: user {user_id: $userId})\n" +
             "RETURN CASE WHEN r IS NULL THEN FALSE ELSE TRUE END AS result")
     CheckResult checkInviteRequest(@RequestParam int userId, @RequestParam int friendId);
     @Query("OPTIONAL MATCH (user1: user {user_id: $friendId})-[r:INVITE_FRIEND]->(user2: user {user_id: $userId})" +
             "DELETE r;")
     void deleteInviteRequest(Integer userId, int friendId);

     @Query("MATCH (user1:user {user_id: $userId})\n" +
             "MATCH (user2:user {user_id: $friendId})\n" +
             "MERGE (user1)-[:FRIEND]->(user2)\n" +
             "MERGE (user2)-[:FRIEND]->(user1)\n")
     void createFriendship(Integer userId, int friendId);

     @Query("OPTIONAL MATCH (user1: user {user_id: $friendId})-[r1:FRIEND]->(user2: user {user_id: $userId})\n" +
             "OPTIONAL MATCH (user1_2: user {user_id: $userId})-[r2:FRIEND]->(user2_2: user {user_id: $friendId})\n" +
             "DELETE r1,r2 ")
     void unfriend(Integer userId, int friendId);
}
