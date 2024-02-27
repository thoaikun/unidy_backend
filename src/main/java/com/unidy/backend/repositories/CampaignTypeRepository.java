package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.relationship.CampaignType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignTypeRepository extends JpaRepository<CampaignType,Integer> {
}
