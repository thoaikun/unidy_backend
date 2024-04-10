package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.ListVolunteerResponse;
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

    @Query(value = """
        SELECT
        new com.unidy.backend.domains.dto.responses.ListVolunteerResponse(
            u.userId,
            u.fullName,
            DATE_FORMAT(FROM_DAYS(DATEDIFF(NOW(),u.dayOfBirth)), '%Y') + 0,
            u.job,
            u.workLocation,
            vjc.timeJoin,
            vjc.status,
            c.campaignId,
            upi.linkImage
        )
        FROM User u
        INNER JOIN VolunteerJoinCampaign vjc
            ON u.userId = vjc.userId
        LEFT JOIN UserProfileImage upi
            ON u.userId = upi.userId
        INNER JOIN Campaign c
            on vjc.campaignId = c.campaignId
        WHERE vjc.status = 'NOT_APPROVE_YET' and c.owner = :organizationId
                AND c.campaignId = :campaignId
        ORDER BY vjc.timeJoin
    """)
    Page<ListVolunteerResponse> getListVolunteerNotApproved(@Param("organizationId") int organizationId, @Param("campaignId") int campaignId, Pageable pageable);

    @Query(value = """
        SELECT
        new com.unidy.backend.domains.dto.responses.ListVolunteerResponse(
            u.userId,
            u.fullName,
            DATE_FORMAT(FROM_DAYS(DATEDIFF(NOW(),u.dayOfBirth)), '%Y') + 0,
            u.job,
            u.workLocation,
            vjc.timeJoin,
            vjc.status,
            c.campaignId,
            upi.linkImage
        )
        FROM User u
        INNER JOIN VolunteerJoinCampaign vjc
            ON u.userId = vjc.userId
        LEFT JOIN UserProfileImage upi
            ON u.userId = upi.userId
        INNER JOIN Campaign c
            on vjc.campaignId = c.campaignId
        WHERE vjc.status = 'APPROVE' and c.owner = :organizationId
                AND c.campaignId = :campaignId
        ORDER BY vjc.timeJoin
    """)
    Page<ListVolunteerResponse> getListVolunteerApproved(@Param("organizationId") int organizationId, @Param("campaignId") int campaignId, Pageable pageable);

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
