package com.unidy.backend.repositories;

import com.miragesql.miragesql.annotation.In;
import com.unidy.backend.domains.entity.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SponsorRepository extends JpaRepository<Sponsor, Integer> {
    Optional<Sponsor> findByUserId(Integer userId);
}
