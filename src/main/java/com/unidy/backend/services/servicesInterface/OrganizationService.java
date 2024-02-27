package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.services.servicesIplm.OrganizationServiceIplm;
import org.springframework.http.ResponseEntity;

public interface OrganizationService {
    ResponseEntity<?> getProfileOrganization(int organizationId);
}
