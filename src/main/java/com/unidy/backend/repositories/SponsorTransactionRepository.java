package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.SponsorTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SponsorTransactionRepository extends JpaRepository<SponsorTransaction, Integer> {
}
