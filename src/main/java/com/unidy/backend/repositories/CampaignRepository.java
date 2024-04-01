package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign,Integer> {
    Campaign findCampaignByCampaignId(int campaignId);

    Integer countCampaignByOwner(Integer userId);
}
