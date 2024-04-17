package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.RelationshipCheckResult;
import com.unidy.backend.domains.dto.responses.InviteFriend;
import com.unidy.backend.domains.dto.responses.RecommendFriendResponse;
import com.unidy.backend.domains.entity.neo4j.UserNode;
import jakarta.transaction.Transactional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface Neo4j_UserRepository extends Neo4jRepository<UserNode,Integer> {
     UserNode findUserNodeByUserId(int userId);

     @Query("""
          MATCH (user1:user {user_id: $userId})
          MATCH (user2:user {user_id: $friendId})
          MERGE (user1)-[:INVITE_FRIEND {request_at: $date}]->(user2);
     """)
     void friendInviteRequest(@Param("userId") int userId, @Param("friendId") int friendId, @Param("date") String date);

     @Query("OPTIONAL MATCH (user1: user {user_id: $friendId})-[r:INVITE_FRIEND]->(user2: user {user_id: $userId}) DELETE r;")
     void deleteInviteRequest(Integer userId, int friendId);

     @Query("""
          MATCH (user1:user {user_id: $userId})
          MATCH (user2:user {user_id: $friendId})
          MERGE (user1)-[:FRIEND]->(user2)
          MERGE (user2)-[:FRIEND]->(user1);
     """)
     void createFriendship(Integer userId, int friendId);

     @Query("OPTIONAL MATCH (user1: user {user_id: $friendId})-[r1:FRIEND]->(user2: user {user_id: $userId})\n" +
             "OPTIONAL MATCH (user1_2: user {user_id: $userId})-[r2:FRIEND]->(user2_2: user {user_id: $friendId})\n" +
             "DELETE r1,r2 ")
     void unfriend(Integer userId, int friendId);

     @Query("""
            MATCH (user : user{user_id : $userId}) <- [r:INVITE_FRIEND] - (user2: user)
            RETURN user2 AS userRequest, r.request_at AS requestAt
            SKIP $skip
            LIMIT $limit;
     """)
     List<InviteFriend> getListInvite(Integer userId, int skip, int limit);

     @Query("""
          MATCH (root: user {user_id: $userId})-[r1:FRIEND*1 .. 4]->(friend: user)-[r2:FRIEND]->(user2: user)
          WHERE NOT (root)-[:FRIEND]->(user2) AND user2 <> root
          OPTIONAL MATCH (root) <- [:FRIEND] - (mutualFriend) - [:FRIEND] -> (user2)
          WITH user2, COLLECT(DISTINCT mutualFriend) AS mutualFriends
          WITH user2, COUNT(mutualFriends) AS countMutualFriends, mutualFriends
          ORDER BY user2.user_id
          RETURN user2 AS fiendSuggest, countMutualFriends AS numOfMutualFriend, mutualFriends
          SKIP $skip
          LIMIT $limit;
     """)
     List<RecommendFriendResponse> getRecommendFriend(@Param("userId") Integer userId, @Param("limit") int limit, @Param("skip") int skip, @Param("rangeEnd") int rangeEnd);

     @Query("""
          MATCH (root :user {user_id: $userId})
          MATCH (root) - [r:FRIEND] -> (friend: user)
          WITH friend
          ORDER BY friend.user_id
          RETURN friend, true as isFriend, false as isFollow
          SKIP $skip
          LIMIT $limit;
     """)
     List<UserNode> getListFriend(Integer userId, int limit, int skip);

     @Query("""
             MATCH (user:user) - [r:FOLLOW_ORGANIZATION] -> (organization:user {user_id: $organizationId})
             return toInteger(user.user_id)
     """)
     List<Integer> getFollowers(int organizationId);

     @Query("""
          MATCH (user1:user {user_id: $userId})
          MATCH (user2:user {user_id: $organizationId})
          MERGE (user1)-[:FOLLOW_ORGANIZATION {request_at: $date}]->(user2);
     """)
     void sendFollowRequest(Integer userId, int organizationId, String date);

     @Query("""
            CALL db.index.fulltext.queryNodes("searchUserIndex", $searchTerm) YIELD node, score
            WITH node, score
            OPTIONAL MATCH (:user {user_id: $userId})-[friend:FRIEND]->(node)
            OPTIONAL MATCH (:user {user_id: $userId})-[follow:FOLLOW_ORGANIZATION]->(node)
            WITH node, CASE WHEN friend IS NULL THEN FALSE ELSE TRUE END AS isFriend, CASE WHEN follow IS NULL THEN FALSE ELSE TRUE END AS isFollow, score
            WHERE node.role = 'ORGANIZATION'
            RETURN node, isFriend, isFollow
            ORDER BY score DESC, node.user_id ASC
            SKIP $skip
            LIMIT $limit;
     """)
     List<UserNode> searchUserBySearchTermAndRoleOrganization(int userId, String searchTerm, int limit, int skip);

     @Query("""
            CALL db.index.fulltext.queryNodes("searchUserIndex", $searchTerm) YIELD node, score
            WITH node, score
            OPTIONAL MATCH (:user {user_id: $userId})-[friend:FRIEND]->(node)
            OPTIONAL MATCH (:user {user_id: $userId})-[follow:FOLLOW_ORGANIZATION]->(node)
            WITH node, CASE WHEN friend IS NULL THEN FALSE ELSE TRUE END AS isFriend, CASE WHEN follow IS NULL THEN FALSE ELSE TRUE END AS isFollow, score
            WHERE node.role IS NULL
            RETURN node, isFriend, isFollow
            ORDER BY score DESC, node.user_id ASC
            SKIP $skip
            LIMIT $limit;
     """)
     List<UserNode> searchUserBySearchTermAndRoleVolunteer(int userId, String searchTerm, int limit, int skip);

     @Query("""
            CALL db.index.fulltext.queryNodes("searchUserIndex", $searchTerm) YIELD node, score
            WITH node, score
            OPTIONAL MATCH (:user {user_id: $userId})-[friend:FRIEND]->(node)
            OPTIONAL MATCH (:user {user_id: $userId})-[follow:FOLLOW_ORGANIZATION]->(node)
            RETURN node, CASE WHEN friend IS NULL THEN FALSE ELSE TRUE END AS isFriend, CASE WHEN follow IS NULL THEN FALSE ELSE TRUE END AS isFollow
            ORDER BY score DESC, node.user_id ASC
            SKIP $skip
            LIMIT $limit;
     """)
     List<UserNode> searchUserBySearchTerm(int userId, String searchTerm, int limit, int skip);

//     @Query("""
//             OPTIONAL MATCH (user1: user {user_id: $userId})-[r:FOLLOW_ORGANIZATION]->(user2: user {user_id: $organizationId, role: 'ORGANIZATION'})
//             RETURN CASE WHEN r IS NULL THEN FALSE ELSE TRUE END AS result
//             """)
//     RelationshipCheckResult checkFollow(int userId, int organizationId);

     @Query("OPTIONAL MATCH (user1: user {user_id: $userId})-[r:INVITE_FRIEND]->(user2: user {user_id: $friendId}) DELETE r;")
     void deleteInvite(Integer userId, int friendId);

     @Query("OPTIONAL MATCH (user1: user {user_id: $friendId})-[r:INVITE_FRIEND]->(user2: user {user_id: $userId}) DELETE r;")
     void declineInviteRequest(Integer userId, int friendId);

     @Query("""
          OPTIONAL MATCH (:user {user_id: $userId})-[r:FRIEND]->(:user {user_id: $otherId})
          OPTIONAL MATCH (:user {user_id: $userId})-[i1:INVITE_FRIEND]->(:user {user_id: $otherId})
          OPTIONAL MATCH (:user {user_id: $userId})<-[i2:INVITE_FRIEND]-(:user {user_id: $otherId})
          OPTIONAL MATCH (:user {user_id: $userId})-[i3:FOLLOW_ORGANIZATION]->(:user {user_id: $otherId, role: 'ORGANIZATION'})
          RETURN CASE WHEN r IS NULL THEN FALSE ELSE TRUE END AS isFriend,
                 CASE WHEN i1 IS NULL THEN FALSE ELSE TRUE END AS isRequesting,
                 CASE WHEN i2 IS NULL THEN FALSE ELSE TRUE END AS isRequested,
                 CASE WHEN i3 IS NULL THEN FALSE ELSE TRUE END AS isFollowed;
     """)
     RelationshipCheckResult checkRelationship(int userId, int otherId);

     @Query("""
             MATCH (user:user {user_id : $userId}) - [r:FOLLOW_ORGANIZATION] -> (organization:user {user_id: $organizationId})
             DELETE r;
             """)
     void unfollowOrganization(int userId, int organizationId);
}
