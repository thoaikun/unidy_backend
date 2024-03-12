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

    @Query(value = """
        SELECT new com.unidy.backend.domains.dto.responses.TransactionResponse(
            t.transactionId,
            t.transactionType,
            t.transactionTime,
            t.transactionAmount,
            t.transactionCode,
            u.userId,
            u.fullName,
            u.email,
            t.campaignId,
            c.description,
            c.donationBudgetReceived,
            c.donationBudget
        )
        FROM Transaction t
        INNER JOIN User u ON t.userId = u.userId
        INNER JOIN Campaign c ON c.campaignId = t.campaignId
        WHERE t.organizationUserId = :organizationUserId
    """)
    List<TransactionResponse> findTransactionByOrganizationId(@Param("organizationUserId") int organizationUserId);

    @Query(value = """
        SELECT DISTINCT new com.unidy.backend.domains.dto.responses.TransactionResponse(
            t.transactionId,
            t.transactionType,
            t.transactionTime,
            t.transactionAmount,
            t.transactionCode,
            u.userId,
            u.fullName,
            u.email,
            t.campaignId,
            c.description,
            c.donationBudgetReceived,
            c.donationBudget
        )
        FROM Transaction t
        INNER JOIN User u ON t.userId = u.userId
        INNER JOIN Campaign c ON c.campaignId = t.campaignId
        WHERE t.organizationUserId = :organizationUserId and c.campaignId = :campaignId
    """)

    List<TransactionResponse> findTransactionByCampaignId(@Param("organizationUserId") int organizationUserId, @Param("campaignId") int campaignId);


    @Query(value = """
        SELECT DISTINCT new com.unidy.backend.domains.dto.responses.TransactionResponse(
            t.transactionId,
            t.transactionType,
            t.transactionTime,
            t.transactionAmount,
            t.transactionCode,
            u.userId,
            u.fullName,
            u.email,
            t.campaignId,
            c.description,
            c.donationBudgetReceived,
            c.donationBudget
        )
        FROM Transaction t
        INNER JOIN User u ON t.userId = u.userId
        INNER JOIN Campaign c ON c.campaignId = t.campaignId
        WHERE u.userId = :userId
    """)

    List<TransactionResponse> findTransactionByUserId(Integer userId);
}
