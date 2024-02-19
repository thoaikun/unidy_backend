package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization,Integer> {
    Optional<Organization> findByOrganizationId(Integer owner);

    Optional<Organization> findByUserId(Integer owner);
}
