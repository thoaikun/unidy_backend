package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.TransactionResponse;
import com.unidy.backend.domains.entity.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("""
            SELECT new com.unidy.backend.domains.dto.responses.TransactionResponse(
                SUM (t.transactionAmount),
                t.organizationUserId,
                t.campaignId,
                u.userId,
                u.fullName,
                upi.linkImage
            )
            FROM Transaction t
            JOIN Campaign c ON t.campaignId = c.campaignId
            JOIN User u ON t.userId = u.userId
            JOIN UserProfileImage upi ON u.userId = upi.userId
            WHERE t.organizationUserId = :organizationUserId
            GROUP BY u.userId
            ORDER BY t.transactionTime DESC
    """)
    List<TransactionResponse> findTransactionsByOrganizationUserIdSortByDate(int organizationUserId, Pageable pageable);

    @Query("""
            SELECT new com.unidy.backend.domains.dto.responses.TransactionResponse(
                SUM (t.transactionAmount),
                t.organizationUserId,
                t.campaignId,
                u.userId,
                u.fullName,
                upi.linkImage
            )
            FROM Transaction t
            JOIN Campaign c ON t.campaignId = c.campaignId
            JOIN User u ON t.userId = u.userId
            JOIN UserProfileImage upi ON u.userId = upi.userId
            WHERE t.organizationUserId = :organizationUserId
            GROUP BY u.userId
            ORDER BY SUM(t.transactionAmount) DESC
    """)
    List<TransactionResponse> findTransactionsByOrganizationUserIdSortByTransactionAmount(int organizationUserId, Pageable pageable);

    @Query("""
        SELECT new com.unidy.backend.domains.dto.responses.TransactionResponse(
            t.transactionId,
            t.transactionType,
            t.transactionTime,
            t.transactionAmount,
            t.transactionCode,
            t.signature,
            t.organizationUserId,
            t.campaignId,
            c,
            u.userId,
            u.fullName,
            upi.linkImage
        )
        FROM Transaction t
        JOIN Campaign c ON t.campaignId = c.campaignId
        JOIN User u ON t.userId = u.userId
        JOIN UserProfileImage upi ON u.userId = upi.userId
        WHERE t.organizationUserId = :organizationUserId AND t.campaignId = :campaignId
    """)
    List<TransactionResponse> findTransactionsByOrganizationUserIdAndCampaignId(int organizationUserId, int campaignId, Pageable pageable);

    @Query("""
        SELECT new com.unidy.backend.domains.dto.responses.TransactionResponse(
            SUM(t.transactionAmount),
            t.organizationUserId,
            t.campaignId,
            u.userId,
            u.fullName,
            upi.linkImage
        )
        FROM Transaction t
        JOIN User u ON t.userId = u.userId
        JOIN UserProfileImage upi ON u.userId = upi.userId
        WHERE t.campaignId = :campaignId
        GROUP BY u.userId
    """)
    List<TransactionResponse> findTransactionsByCampaignId(int campaignId, Pageable pageable);

    @Query("""
        SELECT new com.unidy.backend.domains.dto.responses.TransactionResponse(
                t.transactionId,
                t.transactionType,
                t.transactionTime,
                t.transactionAmount,
                t.transactionCode,
                t.signature,
                t.organizationUserId,
                t.campaignId,
                c,
                u.userId,
                u.fullName,
                upi.linkImage
            )
            FROM Transaction t
            JOIN Campaign c ON t.campaignId = c.campaignId
            JOIN User u ON t.userId = u.userId
            JOIN UserProfileImage upi ON u.userId = upi.userId
            WHERE t.userId = :userId
    """)
    List<TransactionResponse> findTransactionByUserId(@Param("userId") int userId, Pageable pageable);

    @Query("""
        SELECT COALESCE(SUM(t.transactionAmount), 0)
        FROM Transaction t
        WHERE t.organizationUserId = :organizationUserId
    """)
    Integer sumAmountTransactionByOrganizationUserId(Integer organizationUserId);

    @Query("""
        SELECT COALESCE(SUM(t.transactionAmount), 0)
        FROM Transaction t
        WHERE t.userId = :organizationUserId AND t.transactionTime = CURRENT_DATE()
    """)
    Integer sumAmountTransactionByOrganizationUserIdInDay(Integer organizationUserId);
}
