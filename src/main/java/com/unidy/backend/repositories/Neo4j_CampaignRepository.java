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
        OPTIONAL MATCH (organizationNode)-[r_like:LIKE]->(campaign)
        WITH campaign, organizationNode, r, count(r_like) AS likeCount, r_like, isLiked
        RETURN campaign, organizationNode, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked, FALSE AS isJoined
        ORDER BY campaign.create_date DESC, campaign.id ASC
        SKIP $skip
        LIMIT $limit;
    """)
    List<CampaignPostResponse.CampaignPostResponseData> findCampaign(Integer userId, int skip, int limit);

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
        OPTIONAL MATCH (user)-[isLiked:LIKE]->(campaign)
        OPTIONAL MATCH (organizationNode)-[r_like:LIKE]->(campaign)
        WITH campaign, organizationNode, r, count(r_like) AS likeCount, r_like, isLiked
        RETURN campaign, organizationNode, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked, FALSE AS isJoined
        ORDER BY campaign.create_date DESC, campaign.id ASC
        SKIP $skip
        limit $limit;
    """)
    List<CampaignPostResponse.CampaignPostResponseData> findCampaignByOrganizationID(int organizationId, int skip, int limit);

    CampaignNode findCampaignNodeByCampaignId(String campaignId);

    @Query("""
            CALL db.index.fulltext.queryNodes("searchCampaignIndex", $searchTerm) YIELD node, score
            WITH node, score
            MATCH (organizationNode:user {role:"ORGANIZATION"})-[r:HAS_CAMPAIGN]->(campaign:campaign)
            WHERE campaign = node
            OPTIONAL MATCH (user)-[isLiked:LIKE]->(campaign)
            OPTIONAL MATCH (organizationNode)-[r_like:LIKE]->(campaign)
            WITH campaign, organizationNode, r, count(r_like) AS likeCount, r_like, isLiked, score
            RETURN campaign, organizationNode, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked
            ORDER BY score DESC, campaign.create_date DESC, campaign.id ASC
            SKIP $skip
            LIMIT $limit;
    """)
    List<CampaignPostResponse.CampaignPostResponseData> searchCampaign(String searchTerm, int limit, int skip);

    @Query("""
            MATCH (user : user {user_id: $userId})  - [like:LIKE] -> (campaign : campaign {campaign_id : $campaignId})
            DELETE like
            """)
    void cancelLikeCampaign(Integer userId, String campaignId);

    @Query("""
            MATCH (organizationNode:user)-[r:HAS_CAMPAIGN]->(campaign:campaign)
            WHERE campaign.create_date <= $toDate AND campaign.create_date >= $fromDate
            OPTIONAL MATCH (user)-[isLiked:LIKE]->(campaign)
            OPTIONAL MATCH (organizationNode)-[r_like:LIKE]->(campaign)
            WITH campaign, organizationNode, r, count(r_like) AS likeCount, r_like, isLiked
            RETURN campaign, organizationNode, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked, FALSE AS isJoined
            ORDER BY campaign.create_date DESC, campaign.id ASC
            SKIP $skip
            LIMIT $limit;
            """)
    List<CampaignPostResponse.CampaignPostResponseData> findCampaignPostByCampaignDate(Date fromDate, Date toDate, int skip, int limit);

    @Query("""
            MATCH (organizationNode:user)-[r:HAS_CAMPAIGN]->(campaign:campaign)
            WHERE campaign.create_date <= $toDate AND campaign.create_date >= $fromDate AND campaign.status = $status
            OPTIONAL MATCH (user)-[isLiked:LIKE]->(campaign)
            OPTIONAL MATCH (organizationNode)-[r_like:LIKE]->(campaign)
            WITH campaign, organizationNode, r, count(r_like) AS likeCount, r_like, isLiked
            RETURN campaign, organizationNode, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked, FALSE AS isJoined
            ORDER BY campaign.create_date DESC, campaign.id ASC
            SKIP $skip
            LIMIT $limit;
            """)
    List<CampaignPostResponse.CampaignPostResponseData> findCampaignPost(CampaignStatus status, Date fromDate, Date toDate, int skip, int limit);

    @Query("""
            MATCH (organizationNode:user)-[r:HAS_CAMPAIGN]->(campaign:campaign {campaign_id: $campaignId})
            OPTIONAL MATCH (user)-[isLiked:LIKE]->(campaign)
            OPTIONAL MATCH (organizationNode)-[r_like:LIKE]->(campaign)
            WITH campaign, organizationNode, r, count(r_like) AS likeCount, r_like, isLiked
            RETURN campaign, organizationNode, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked, FALSE AS isJoined
            """)
    CampaignPostResponse.CampaignPostResponseData findCampaignPostByCampaignId(String campaignId);
}
