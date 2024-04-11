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
    @Query("""
        SELECT new com.unidy.backend.domains.entity.Campaign(
            c.campaignId,
            c.title,
            c.description,
            c.numberVolunteer,
            c.numberVolunteerRegistered,
            c.donationBudget,
            c.donationBudgetReceived,
            c.startDate,
            c.endDate,
            c.timeTakePlace,
            c.location,
            c.status,
            c.createDate,
            c.updateDate,
            c.updateBy,
            c.owner,
            c.hashTag,
            c.link_image,
            ct,
            o
        )
        FROM Campaign c
        JOIN Organization o
            ON c.owner = o.userId
        JOIN CampaignType ct
            ON c.campaignId = ct.campaignId
        WHERE c.campaignId = :campaignId
    """)
    Campaign findCampaignByCampaignId(int campaignId);

    Integer countCampaignByOwner(Integer userId);

    List<Campaign> getCampaignsByOwner(Integer organizationId, Pageable pageable);

    @Query("""
        SELECT new com.unidy.backend.domains.entity.Campaign(
            c.campaignId,
            c.title,
            c.description,
            c.numberVolunteer,
            c.numberVolunteerRegistered,
            c.donationBudget,
            c.donationBudgetReceived,
            c.startDate,
            c.endDate,
            c.timeTakePlace,
            c.location,
            c.status,
            c.createDate,
            c.updateDate,
            c.updateBy,
            c.owner,
            c.hashTag,
            c.link_image,
            ct,
            o
        )
        FROM Campaign c
        JOIN Organization o
            ON c.owner = o.userId
        JOIN CampaignType ct
            ON c.campaignId = ct.campaignId
        WHERE c.status = :status AND c.createDate BETWEEN :fromDate AND :toDate
    """)
    Page<Campaign> getCampaignsByStatusAndCreateDateBetween(String status, Date fromDate, Date toDate, Pageable pageable);

    @Query("""
        SELECT new com.unidy.backend.domains.entity.Campaign(
            c.campaignId,
            c.title,
            c.description,
            c.numberVolunteer,
            c.numberVolunteerRegistered,
            c.donationBudget,
            c.donationBudgetReceived,
            c.startDate,
            c.endDate,
            c.timeTakePlace,
            c.location,
            c.status,
            c.createDate,
            c.updateDate,
            c.updateBy,
            c.owner,
            c.hashTag,
            c.link_image,
            ct,
            o
        )
        FROM Campaign c
        JOIN Organization o
            ON c.owner = o.userId
        JOIN CampaignType ct
            ON c.campaignId = ct.campaignId
        WHERE c.createDate BETWEEN :fromDate AND :toDate
    """)
    Page<Campaign> getCampaignsByCreateDateBetween(Date fromDate, Date toDate, Pageable pageable);
}
