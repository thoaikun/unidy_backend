package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.ListVolunteerResponse;
import com.unidy.backend.domains.entity.Organization;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization,Integer> {
    @Query(value = "")
    Optional<Organization> findByOrganizationId(Integer owner);

    Optional<Organization> findByUserId(Integer owner);
    @Query(value = """
        SELECT
        new com.unidy.backend.domains.dto.responses.ListVolunteerResponse(
            u.userId,
            v.volunteerId,
            u.fullName,
            u.address,
            u.email,
            vjc.timeJoin,
            vjc.status,
            c.campaignId,
            c.description
        )
        FROM
            Volunteer v
        INNER JOIN User u
            ON v.userId = u.userId
        INNER JOIN VolunteerJoinCampaign vjc
            ON v.volunteerId = vjc.volunteerId
        INNER JOIN Campaign c
            on vjc.campaignId = c.campaignId
        WHERE vjc.status = 'NOT_APPROVE_YET' and c.owner = :organizationId
                AND c.campaignId = :campaignId
        ORDER BY vjc.timeJoin
        """)
    List<ListVolunteerResponse> getListVolunteerNotApproved( @Param("organizationId") int organizationId,
                                                             @Param("campaignId") int campaignId
                                                            ,Pageable pageable);

    @Query(value = """
    SELECT
        new com.unidy.backend.domains.dto.responses.ListVolunteerResponse(
            u.userId,
            v.volunteerId,
            u.fullName,
            u.address,
            u.email,
            vjc.timeJoin,
            vjc.status,
            c.campaignId,
            c.description
        )
    FROM
        Volunteer v
    INNER JOIN User u
        ON v.userId = u.userId
    INNER JOIN VolunteerJoinCampaign vjc
        ON v.volunteerId = vjc.volunteerId
    INNER JOIN Campaign c
        ON vjc.campaignId = c.campaignId
    WHERE vjc.status = 'APPROVED' AND c.owner = :organizationId
        AND c.campaignId = :campaignId
    ORDER BY vjc.timeJoin
    """)
    List<ListVolunteerResponse> getListVolunteerApproved(
            @Param("organizationId") int organizationId,
            @Param("campaignId") int campaignId,
            Pageable pageable
    );
}

