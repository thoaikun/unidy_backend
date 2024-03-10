package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.VolunteerCampaignResponse;
import com.unidy.backend.domains.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization,Integer> {
    @Query(value = "")
    Optional<Organization> findByOrganizationId(Integer owner);

    @Query(value = "")
    Optional<Organization> findByUserId(Integer owner);
//    @Query(value = "")
//    List<VolunteerCampaignResponse> getListVolunteerNotApproved();
//
//    @Query(value = "")
//    List<VolunteerCampaignResponse> getListVolunteerApproved();
}
