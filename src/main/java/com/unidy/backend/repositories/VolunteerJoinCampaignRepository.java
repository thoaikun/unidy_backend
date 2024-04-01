package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.VolunteerJoinResponse;
import com.unidy.backend.domains.entity.VolunteerJoinCampaign;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface VolunteerJoinCampaignRepository extends JpaRepository<VolunteerJoinCampaign, Integer> {
    VolunteerJoinCampaign findVolunteerJoinCampaignByUserIdAndCampaignId(Integer userId, int campaignId);

    List<VolunteerJoinCampaign> findVolunteerJoinCampaignByCampaignIdAndUserIdIn(int campaignId, Collection<Integer> volunteerIds);

    @Modifying
    @Transactional
    @Query("""
            UPDATE VolunteerJoinCampaign vjc
            SET vjc.status = 'APPROVE'
            WHERE vjc.userId in :volunteerIds AND vjc.campaignId = :campaignId
    """)
    void approveVolunteerJoinCampaignByCampaignIdAndUserIdIn(int campaignId, Collection<Integer> volunteerIds);

    @Modifying
    @Transactional
    @Query("""
            UPDATE VolunteerJoinCampaign vjc
            SET vjc.status = 'REJECT'
            WHERE vjc.userId in :volunteerIds AND vjc.campaignId = :campaignId
    """)
    void rejectVolunteerJoinCampaignByCampaignIdAndUserIdIn(int campaignId, Collection<Integer> volunteerIds);

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

    List<VolunteerJoinCampaign> findUserIdsByCampaignIdAndStatus(int campaignId, String status);

    @Query("""
            SELECT COUNT(vjc)
            FROM VolunteerJoinCampaign vjc
            JOIN Campaign c
                ON vjc.campaignId = c.campaignId
            WHERE c.owner = :organizationId
            AND vjc.status = 'APPROVE'
    """)
    Integer countVolunteerByOrganizationId(Integer organizationId);


}
