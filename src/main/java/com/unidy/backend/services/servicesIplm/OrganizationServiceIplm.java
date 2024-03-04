package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.entity.Organization;
import com.unidy.backend.repositories.OrganizationRepository;
import com.unidy.backend.services.servicesInterface.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganizationServiceIplm implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    public ResponseEntity<?> getProfileOrganization(int organizationId){
        try {
            Optional<Organization> organization = organizationRepository.findByUserId(organizationId);
            return ResponseEntity.ok().body(organization);
        } catch (Exception exception){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something Error"));
        }
    }
}
