package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.VolunteerJoinCampaign;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VolunteerJoinCampaignRepository extends JpaRepository<VolunteerJoinCampaign, Integer> {
    VolunteerJoinCampaign findVolunteerJoinCampaignByUserIdAndCampaignId(Integer userId, int campaignId);

    List<VolunteerJoinCampaign> findVolunteerJoinCampaignByUserId(Integer userId,  Pageable pageable);

    List<VolunteerJoinCampaign> findVolunteerJoinCampaignByCampaignIdInAndUserId(List<Integer> campaignIds, Integer userId);
}
