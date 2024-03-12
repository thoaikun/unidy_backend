package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.TransactionResponse;
import com.unidy.backend.domains.entity.Transaction;
import jakarta.transaction.Transactional;
import org.hibernate.annotations.NamedNativeQueries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

////    @Query(value = """
////                    SELECT new com.unidy.backend.domains.dto.responses.TransactionResponse(t.transactionId,t.transactionType,t.transactionTime,t.transactionAmount,t.transactionCode,s.sponsorId,s.sponsorName,s.email,t.campaignId,c.description,c.donationBudgetReceived,c.donationBudget) FROM Sponsor s INNER JOIN SponsorTransaction st ON s.sponsorId = st.sponsorId INNER JOIN Transaction t ON t.transactionId = st.transactionId INNER JOIN Campaign c ON c.campaignId = t.campaignId WHERE t.organizationUserId = :organizationUserId
////                    """)
//@Query(value = "")
//
//    List<TransactionResponse> findTransactionByOrganizationId(@Param("organizationUserId") int organizationUserId);
////    @Query(value = """
////                    SELECT new com.unidy.backend.domains.dto.responses.TransactionResponse(t.transactionId,t.transactionType,t.transactionTime,t.transactionAmount,t.transactionCode,s.sponsorId,s.sponsorName,s.email,t.campaignId,c.description,c.donationBudgetReceived,c.donationBudget) FROM Sponsor s INNER JOIN SponsorTransaction st ON s.sponsorId = st.sponsorId INNER JOIN Transaction t ON t.transactionId = st.transactionId INNER JOIN Campaign c ON c.campaignId = t.campaignId WHERE t.organizationUserId = :organizationUserId AND c.campaignId = :campaignId
////                    """)
//
//    @Query(value = "")
//
//    List<TransactionResponse> findTransactionByCampaignId(@Param("organizationUserId") int organizationUserId, @Param("campaignId") int campaignId);
//
//
////    @Query(value = """
////                    SELECT new com.unidy.backend.domains.dto.responses.TransactionResponse(t.transactionId,t.transactionType,t.transactionTime,t.transactionAmount,t.transactionCode,s.sponsorId,s.sponsorName,s.email,t.campaignId,c.description,c.donationBudgetReceived,c.donationBudget) FROM Sponsor s INNER JOIN SponsorTransaction st ON s.sponsorId = st.sponsorId INNER JOIN Transaction t ON t.transactionId = st.transactionId INNER JOIN Campaign c ON c.campaignId = t.campaignId WHERE s.userId = :userId
////                    """)
//
//    @Query(value = "")
//    List<TransactionResponse> findTransactionByUserId(Integer userId);
}
