package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization,Integer> {
}
