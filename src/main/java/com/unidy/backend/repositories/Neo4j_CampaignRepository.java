package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.CampaignPostResponse;
import com.unidy.backend.domains.entity.neo4j.CampaignNode;
import jakarta.transaction.Transactional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public interface Neo4j_CampaignRepository  extends Neo4jRepository<CampaignNode,String> {

    @Query("MATCH (user:user {user_id: $userId})-[:FOLLOW_ORGANIZATION]->(organizationNode:user {role:\"ORGANIZATION\"})-[r:HAS_CAMPAIGN]->(campaign:campaign)\n" +
            "WHERE campaign.create_date < $cursor\n" +
            "OPTIONAL MATCH (user)-[isLiked:LIKE]->(campaign)\n" +
            "OPTIONAL MATCH (organizationNode)-[r_like:LIKE]->(campaign)\n" +
            "WITH campaign, organizationNode, r, count(r_like) AS likeCount, r_like, isLiked\n" +
            "RETURN campaign, organizationNode, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked\n" +
            "ORDER BY campaign.create_date DESC\n" +
            "LIMIT $limit;")
    List<CampaignPostResponse.CampaignPostResponseData> findCampaign(Integer userId, String cursor, int limit);

    @Query("MATCH (organizationNode:user {role:\"ORGANIZATION\"})-[r:HAS_CAMPAIGN]->(campaign:campaign) WHERE campaign.campaign_id IN $campaignIds\n" +
            "OPTIONAL MATCH (user)-[isLiked:LIKE]->(campaign)\n" +
            "OPTIONAL MATCH (organizationNode)-[r_like:LIKE]->(campaign)\n" +
            "WITH campaign, organizationNode, r, count(r_like) AS likeCount, r_like, isLiked\n" +
            "RETURN campaign, organizationNode, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked\n" +
            "ORDER BY campaign.create_date DESC;")
    List<CampaignPostResponse.CampaignPostResponseData> findCampaignPostByCampaignIds(String[] campaignIds);


    @Query("MATCH (organizationNode:user {role:\"ORGANIZATION\", user_id : $organizationId})-[r:HAS_CAMPAIGN]->(campaign:campaign) \n" +
            "WHERE campaign.create_date < $cursor\n" +
            "OPTIONAL MATCH (user)-[isLiked:LIKE]->(campaign)\n" +
            "OPTIONAL MATCH (organizationNode)-[r_like:LIKE]->(campaign)\n" +
            "WITH campaign, organizationNode, r, count(r_like) AS likeCount, r_like, isLiked\n" +
            "RETURN campaign, organizationNode, r, likeCount, r_like, CASE WHEN isLiked IS NOT NULL THEN true ELSE false END AS isLiked\n" +
            "ORDER BY campaign.create_date DESC\n" +
            "limit $limit;\n")
    List<CampaignPostResponse.CampaignPostResponseData> findCampaignByOrganizationID(int organizationId, String cursor, int limit);

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
}
