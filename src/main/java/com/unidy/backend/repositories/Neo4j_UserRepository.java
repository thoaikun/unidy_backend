package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.CheckResult;
import com.unidy.backend.domains.dto.responses.InviteFriend;
import com.unidy.backend.domains.dto.responses.RecommendFriendResponse;
import com.unidy.backend.domains.entity.UserNode;
import jakarta.transaction.Transactional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
@Transactional
public interface Neo4j_UserRepository extends Neo4jRepository<UserNode,Integer> {
     UserNode findUserNodeByUserId(int userId);
     @Query("MATCH (user1:user {user_id: $userId})\n" +
             "MATCH (user2:user {user_id: $friendId})\n" +
             "MERGE (user1)-[:INVITE_FRIEND {request_at: $date}]->(user2)\n")
     void friendInviteRequest(@Param("userId") int userId, @Param("friendId") int friendId, @Param("date") String date);

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

     @Query("MATCH (user : user{user_id : $userId}) <- [r:INVITE_FRIEND] - (user2: user)\n" +
             "WHERE r.request_at  < $cursor\n" +
             "RETURN user2 AS userRequest, r.request_at AS requestAt\n" +
             "LIMIT $limit;")
     List<InviteFriend> getListInvite(Integer userId, String cursor, int limit);

     @Query("MATCH (root: user {user_id: $userId})-[r1:FRIEND*1 .. 4]->(friend: user)-[r2:FRIEND]->(user2: user)\n" +
             "WHERE NOT (root)-[:FRIEND]->(user2) AND user2 <> root\n" +
             "OPTIONAL MATCH (root) <- [:FRIEND] - (mutualFriend) - [:FRIEND] -> (user2)\n" +
             "WITH user2, COLLECT(DISTINCT mutualFriend) AS mutualFriends\n" +
             "WITH user2, COUNT(mutualFriends) AS countMutualFriends, mutualFriends\n" +
             "ORDER BY user2.user_id\n" +
             "RETURN user2 AS fiendSuggest, countMutualFriends AS numOfMutualFriend, mutualFriends\n" +
             "SKIP $skip\n" +
             "LIMIT $limit;\n")
     List<RecommendFriendResponse> getRecommendFriend(@Param("userId") Integer userId, @Param("limit") int limit, @Param("skip") int skip, @Param("rangeEnd") int rangeEnd);

     @Query("MATCH (root :user {user_id: $userId})\n" +
             "OPTIONAL MATCH (root) - [r:FRIEND] -> (friend: user)\n" +
             "WHERE friend.user_id > $cursor\n" +
             "WITH friend\n" +
             "ORDER BY friend.user_id\n" +
             "RETURN friend\n" +
             "LIMIT $limit;")
     List<UserNode> getListFriend(Integer userId, int limit, int cursor);

     @Query(" OPTIONAL MATCH (user1: user {user_id: $userId})-[r:FOLLOW_ORGANIZATION]->(user2: user {user_id: $organizationId})\n" +
             "RETURN CASE WHEN r IS NULL THEN FALSE ELSE TRUE END AS result")
     CheckResult checkFollowRequest(Integer userId, int organizationId);

     @Query("MATCH (user1:user {user_id: $userId})\n" +
             "MATCH (user2:user {user_id: $organizationId})\n" +
             "MERGE (user1)-[:FOLLOW_ORGANIZATION {request_at: $date}]->(user2)\n")
     void sendFollowRequest(Integer userId, int organizationId, String date);
}
