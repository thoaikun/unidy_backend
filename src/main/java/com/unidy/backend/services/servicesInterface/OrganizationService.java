package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.notification.NotificationDto;
import com.unidy.backend.services.servicesIplm.OrganizationServiceIplm;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface OrganizationService {
    ResponseEntity<?> getProfileOrganization(int organizationI);
    ResponseEntity<?> getListVolunteer();
    ResponseEntity<?> approveVolunteer(Principal connectedUser, int volunteerId, int campaignId);
    ResponseEntity<?> getListVolunteerApproved(int organizationId, int campaignId, int pageNumber, int pageSize);
    ResponseEntity<?> getListVolunteerNotApproved(int organizationId, int campaignId,int pageNumber, int pageSize);
    ResponseEntity<?> getListTransaction(int organizationUserId);
    ResponseEntity<?> getListCampaignTransaction(Integer userId, int campaignId);

    ResponseEntity<?> sendNotifyToMember(Principal connectedUser, NotificationDto notificationDto);
}
