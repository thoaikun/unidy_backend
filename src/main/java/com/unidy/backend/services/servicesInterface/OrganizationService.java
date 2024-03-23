package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.notification.NotificationDto;
import com.unidy.backend.services.servicesIplm.OrganizationServiceIplm;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface OrganizationService {
    ResponseEntity<?> getProfileOrganization(int organizationI);
    ResponseEntity<?> getListVolunteer();
    ResponseEntity<?> approveVolunteer(Principal connectedUser, int volunteerId, int campaignId);
    ResponseEntity<?> getListVolunteerApproved(int organizationId, int campaignId, int offset, int limit);
    ResponseEntity<?> getListVolunteerNotApproved(int organizationId, int campaignId,int offset, int limit);
    ResponseEntity<?> getListTransaction(int organizationUserId, int offset, int limit);
    ResponseEntity<?> getListCampaignTransaction(Integer userId, int campaignId, int offset, int limit);

    ResponseEntity<?> sendNotifyToMember(Principal connectedUser, NotificationDto notificationDto);
}
