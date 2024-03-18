package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.CampaignPostResponse;
import com.unidy.backend.domains.dto.responses.CampaignResponse;
import com.unidy.backend.domains.entity.CampaignNode;
import jakarta.transaction.Transactional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public interface Neo4j_CampaignRepository  extends Neo4jRepository<CampaignNode,String> {

    @Query("""
        MATCH (user:user {user_id: $userId})-[:FOLLOW_ORGANIZATION]->(organizationNode:user {role:"ORGANIZATION"})-[r:HAS_CAMPAIGN]->(campaign:campaign)
        WHERE campaign.create_date < $cursor
        OPTIONAL MATCH (user)-[isLiked:LIKE]->(campaign)
        OPTIONAL MATCH (organizationNode)-[r_like:LIKE]->(campaign)
        WITH campaign, organizationNode, r, count(r_like) AS likeCount, r_like, isLiked
        RETURN campaign, organizationNode, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked, FALSE AS isJoined
        ORDER BY campaign.create_date DESC
        LIMIT $limit;
    """)
    List<CampaignPostResponse.CampaignPostResponseData> findCampaign(Integer userId, String cursor, int limit);

    @Query("""
        MATCH (organizationNode:user {role:"ORGANIZATION"})-[r:HAS_CAMPAIGN]->(campaign:campaign) WHERE campaign.campaign_id IN $campaignIds
        OPTIONAL MATCH (user)-[isLiked:LIKE]->(campaign)
        OPTIONAL MATCH (organizationNode)-[r_like:LIKE]->(campaign)
        WITH campaign, organizationNode, r, count(r_like) AS likeCount, r_like, isLiked
        RETURN campaign, organizationNode, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked, FALSE AS isJoined
        ORDER BY campaign.create_date DESC;
    """)
    List<CampaignPostResponse.CampaignPostResponseData> findCampaignPostByCampaignIds(String[] campaignIds);


    @Query("""
        MATCH (organizationNode:user {role:"ORGANIZATION", user_id : $organizationId})-[r:HAS_CAMPAIGN]->(campaign:campaign)
        WHERE campaign.create_date < $cursor
        OPTIONAL MATCH (user)-[isLiked:LIKE]->(campaign)
        OPTIONAL MATCH (organizationNode)-[r_like:LIKE]->(campaign)
        WITH campaign, organizationNode, r, count(r_like) AS likeCount, r_like, isLiked
        RETURN campaign, organizationNode, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked, FALSE AS isJoined
        ORDER BY campaign.create_date DESC
        limit $limit;
    """)
    List<CampaignPostResponse.CampaignPostResponseData> findCampaignByOrganizationID(int organizationId, String cursor, int limit);

    CampaignNode findCampaignNodeByCampaignId(String campaignId);
}
