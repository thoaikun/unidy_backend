package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.Campaign;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign,Integer> {
    Campaign findCampaignByCampaignId(int campaignId);

    Integer countCampaignByOwner(Integer userId);

    List<Campaign> getCampaignsByOwner(Integer organizationId, Pageable pageable);
}
