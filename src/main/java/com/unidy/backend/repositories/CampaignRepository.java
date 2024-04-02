package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.VolunteerDonationResponse;
import com.unidy.backend.domains.entity.Campaign;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign,Integer> {
    Campaign findCampaignByCampaignId(int campaignId);


    @Query("""
    SELECT new com.unidy.backend.domains.dto.responses.VolunteerDonationResponse(
           user.userId,
               user.fullName,
               CASE WHEN achievement.content IS NULL THEN 'Nhà tài trợ' ELSE achievement.content END,
               transaction.transactionId,
               sum(transaction.transactionAmount)
    )
    FROM
        User user
    INNER JOIN
        Transaction transaction ON user.userId = transaction.userId
    LEFT JOIN
        Achievement achievement ON user.userId = achievement.volunteer_id
    INNER JOIN
        Campaign campaign ON transaction.campaignId = campaign.campaignId
    WHERE
        campaign.campaignId = :campaignId
    ORDER BY sum(transaction.transactionAmount)
""")
    List<VolunteerDonationResponse> getListDonationByCampaignId(String campaignId, Pageable pageable);


}
