package com.unidy.backend.repositories;

import com.unidy.backend.domains.Type.CampaignStatus;
import com.unidy.backend.domains.dto.responses.CampaignPostResponse;
import com.unidy.backend.domains.entity.neo4j.CampaignNode;
import jakarta.transaction.Transactional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Transactional
@Repository
public interface Neo4j_CampaignRepository  extends Neo4jRepository<CampaignNode,String> {

    @Query("""
        MATCH (user:user {user_id: $userId})-[:FOLLOW_ORGANIZATION]->(organizationNode:user {role:"ORGANIZATION"})-[r:HAS_CAMPAIGN]->(campaign:campaign)
        OPTIONAL MATCH (user)-[isLiked:LIKE]->(campaign)
        OPTIONAL MATCH (:user)-[r_like:LIKE]->(campaign)
        OPTIONAL MATCH (campaign) - [has_comment:HAS_COMMENT] -> (comment: comment)
        WITH campaign, organizationNode, r, count(r_like) AS likeCount, r_like, isLiked, has_comment
        RETURN campaign, organizationNode, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked, FALSE AS isJoined, count(has_comment) as numberComments
        ORDER BY campaign.create_date DESC, campaign.id ASC
        SKIP $skip
        LIMIT $limit;
    """)
    List<CampaignPostResponse.CampaignPostResponseData> findCampaign(Integer userId, int skip, int limit);

    @Query("""
        MATCH (organizationNode:user {role:"ORGANIZATION"})-[r:HAS_CAMPAIGN]->(campaign:campaign) WHERE campaign.campaign_id IN $campaignIds
        OPTIONAL MATCH (:user {user_id: $userId})-[isLiked:LIKE]->(campaign)
        OPTIONAL MATCH (:user)-[r_like:LIKE]->(campaign)
        OPTIONAL MATCH (campaign) - [has_comment:HAS_COMMENT] -> (comment: comment)
        WITH campaign, organizationNode, r, count(r_like) AS likeCount, r_like, isLiked, has_comment
        RETURN campaign, organizationNode, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked, FALSE AS isJoined, count(has_comment) as numberComments
        ORDER BY campaign.create_date DESC;
    """)
    List<CampaignPostResponse.CampaignPostResponseData> findCampaignPostByCampaignIds(int userId, String[] campaignIds);


    @Query("""
        MATCH (organizationNode:user {role:"ORGANIZATION", user_id : $organizationId})-[r:HAS_CAMPAIGN]->(campaign:campaign)
        OPTIONAL MATCH (:user {user_id: $userId})-[isLiked:LIKE]->(campaign)
        OPTIONAL MATCH (:user)-[r_like:LIKE]->(campaign)
        OPTIONAL MATCH (campaign) - [has_comment:HAS_COMMENT] -> (comment: comment)
        WITH campaign, organizationNode, r, count(r_like) AS likeCount, r_like, isLiked,has_comment
        RETURN campaign, organizationNode, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked, FALSE AS isJoined, count(has_comment) as numberComments
        ORDER BY campaign.create_date DESC, campaign.id ASC
        SKIP $skip
        limit $limit;
    """)
    List<CampaignPostResponse.CampaignPostResponseData> findCampaignByOrganizationID(int userId, int organizationId, int skip, int limit);

    CampaignNode findCampaignNodeByCampaignId(String campaignId);

    @Query("""
          OPTIONAL MATCH (user1:user {user_id: $userId})-[r:LIKE]->(campaign:campaign {campaign_id: $campaignId})
          RETURN CASE WHEN r IS NOT NULL THEN true ELSE false END
     """)
    boolean isLikedCampaign(int userId, String campaignId);

    @Query("""
            CALL db.index.fulltext.queryNodes("searchCampaignIndex", $searchTerm) YIELD node, score
            WITH node, score
            MATCH (organizationNode:user {role:"ORGANIZATION"})-[r:HAS_CAMPAIGN]->(campaign:campaign)
            WHERE campaign = node
            OPTIONAL MATCH (:user {user_id: $userId})-[isLiked:LIKE]->(campaign)
            OPTIONAL MATCH (:user)-[r_like:LIKE]->(campaign)
            OPTIONAL MATCH (campaign) - [has_comment:HAS_COMMENT] -> (comment: comment)
            WITH campaign, organizationNode, r, count(r_like) AS likeCount, r_like, isLiked, score, has_comment
            RETURN campaign, organizationNode, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked,count(has_comment) as numberComments
            ORDER BY score DESC, campaign.create_date DESC, campaign.id ASC
            SKIP $skip
            LIMIT $limit;
    """)
    List<CampaignPostResponse.CampaignPostResponseData> searchCampaign(int userId, String searchTerm, int limit, int skip);

    @Query("""
            MATCH (user : user {user_id: $userId})  - [like:LIKE] -> (campaign : campaign {campaign_id : $campaignId})
            DELETE like
            """)
    void cancelLikeCampaign(Integer userId, String campaignId);
}
