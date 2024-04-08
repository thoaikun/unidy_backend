package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.ListVolunteerResponse;
import com.unidy.backend.domains.dto.responses.OrganizationInformation;
import com.unidy.backend.domains.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization,Integer> {
    Optional<Organization> findByUserId(Integer owner);

    @Query("""
    SELECT
        new com.unidy.backend.domains.dto.responses.OrganizationInformation(
            o.userId,
            o.organizationName,
            o.address,
            o.phone,
            o.email,
            o.country,
            upi.linkImage,
            o.firebaseTopic,
            false
        )
        FROM Organization o
        LEFT JOIN UserProfileImage upi
            ON o.userId = upi.userId
        WHERE o.userId = :organizationId
    """)
    OrganizationInformation getOrganizationInformation(int organizationId);

    Organization findByOrganizationId(int organizationId);
}

