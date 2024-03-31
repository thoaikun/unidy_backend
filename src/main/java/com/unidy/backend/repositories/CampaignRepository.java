package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.VolunteerDonationResponse;
import com.unidy.backend.domains.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign,Integer> {
    Campaign findCampaignByCampaignId(int campaignId);


    @Query("""
            SELECT new com.unidy.backend.domains.dto.responses.VolunteerDonationResponse(
                user.userId
               ,user.fullName
               ,achievement.content
               ,transaction.transactionId
               ,transaction.transactionAmount
            )
            FROM
            User user 
            INNER JOIN Transaction transaction
            ON user.userId = transaction.userId
            INNER JOIN Volunteer volunteer
            ON user.userId = volunteer.userId
            INNER JOIN Achievement achievement
            ON volunteer.volunteerId = achievement.volunteer_id
            INNER JOIN Campaign campaign
            ON transaction.campaignId = campaign.campaignId
            WHERE campaign.campaignId = :campaignId 
            """)
    List<VolunteerDonationResponse> getListDonationByCampaignId(String campaignId);
}
