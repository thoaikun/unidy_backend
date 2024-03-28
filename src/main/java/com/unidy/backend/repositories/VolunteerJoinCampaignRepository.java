package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.VolunteerJoinResponse;
import com.unidy.backend.domains.entity.VolunteerJoinCampaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VolunteerJoinCampaignRepository extends JpaRepository<VolunteerJoinCampaign, Integer> {
    VolunteerJoinCampaign findVolunteerJoinCampaignByUserIdAndCampaignId(Integer userId, int campaignId);

    List<VolunteerJoinCampaign> findVolunteerJoinCampaignByUserId(Integer userId, Pageable pageable);

    List<VolunteerJoinCampaign> findVolunteerJoinCampaignByCampaignIdInAndUserId(List<Integer> campaignIds, Integer userId);

    @Query("""

            SELECT new com.unidy.backend.domains.dto.responses.VolunteerJoinResponse(
          user.userId,
          user.fullName,
          user.workLocation,
          Year(CURRENT_DATE()) - YEAR(user.dayOfBirth)
      )
      FROM User user
      INNER JOIN VolunteerJoinCampaign volunteerJoinCampaign
      ON volunteerJoinCampaign.userId = user.userId
      WHERE volunteerJoinCampaign.campaignId = :campaignId
      AND volunteerJoinCampaign.status = 'APPROVE'
       """)
    List<VolunteerJoinResponse> findVolunteerJoinCampaignByCampaignId(@Param("campaignId") int campaignId);



}
