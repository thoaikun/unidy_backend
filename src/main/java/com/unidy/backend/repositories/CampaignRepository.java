package com.unidy.backend.repositories;

import com.unidy.backend.domains.Type.CampaignStatus;
import com.unidy.backend.domains.entity.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign,Integer> {
    Campaign findCampaignByCampaignId(int campaignId);

    Integer countCampaignByOwner(Integer userId);

    List<Campaign> getCampaignsByOwner(Integer organizationId, Pageable pageable);

    Page<Campaign> getCampaignsByStatusAndCreateDateBetween(String status, Date fromDate, Date toDate, Pageable pageable);

    Page<Campaign> getCampaignsByCreateDateBetween(Date fromDate, Date toDate, Pageable pageable);
}
