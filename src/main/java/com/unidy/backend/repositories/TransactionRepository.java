package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.TransactionResponse;
import com.unidy.backend.domains.entity.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findTransactionsByOrganizationUserId(int organizationUserId, Pageable pageable);

    List<Transaction> findTransactionsByOrganizationUserIdAndCampaignId(int organizationUserId, int campaignId, Pageable pageable);

    List<Transaction> findTransactionByUserId(@Param("userId") int userId, Pageable pageable);
}
