package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.CampaignNode;
import jakarta.transaction.Transactional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public interface Neo4j_CampaignRepository  extends Neo4jRepository<CampaignNode,String> {
}
