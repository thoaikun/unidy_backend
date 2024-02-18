package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.VolunteerJoinCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VolunteerJoinCampaignRepository extends JpaRepository<VolunteerJoinCampaign, Integer> {
    VolunteerJoinCampaign findVolunteerJoinCampaignByVolunteerIdAndCampaignId(Integer volunteerId, int campaignId);
}
