package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Integer> {
    Settlement findBySettlementId(int settlementId);
}
