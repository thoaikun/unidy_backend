package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.services.servicesIplm.OrganizationServiceIplm;
import org.springframework.http.ResponseEntity;

public interface OrganizationService {
    ResponseEntity<?> getProfileOrganization(int organizationId);
    ResponseEntity<?> getListVolunteer();
    ResponseEntity<?> approveVolunteer(int volunteerId);
    ResponseEntity<?> getListVolunteerApproved(int organizationId);
    ResponseEntity<?> getListVolunteerNotApproved(int organizationId);
    ResponseEntity<?> getListTransaction(int organizationUserId);
    ResponseEntity<?> getListCampaignTransaction(Integer userId, int campaignId);
}
