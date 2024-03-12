package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.services.servicesIplm.OrganizationServiceIplm;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface OrganizationService {
    ResponseEntity<?> getProfileOrganization(int organizationId);
    ResponseEntity<?> getListVolunteer();
    ResponseEntity<?> approveVolunteer(Principal connectedUser, int volunteerId, int campaignId);
    ResponseEntity<?> getListVolunteerApproved(int organizationId, int campaignId);
    ResponseEntity<?> getListVolunteerNotApproved(int organizationId, int campaignId);
    ResponseEntity<?> getListTransaction(int organizationUserId);
    ResponseEntity<?> getListCampaignTransaction(Integer userId, int campaignId);
}
