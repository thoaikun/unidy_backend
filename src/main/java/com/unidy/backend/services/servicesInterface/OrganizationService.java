package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.notification.NotificationDto;
import com.unidy.backend.services.servicesIplm.OrganizationServiceIplm;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface OrganizationService {
    ResponseEntity<?> getProfileOrganization(Principal connectedUser);
    ResponseEntity<?> getProfileOrganization(Principal connectedUser, int organizationId);
    ResponseEntity<?> getListVolunteer(Principal connectedUser, int campaignId);
    ResponseEntity<?> approveVolunteer(Principal connectedUser, int campaignId, List<Integer> volunteerIds);
    ResponseEntity<?> rejectVolunteer(Principal connectedUser, int campaignId, List<Integer> volunteerIds);
    ResponseEntity<?> getListVolunteerApproved(int organizationId, int campaignId, int pageNumber, int pageSize);
    ResponseEntity<?> getListVolunteerNotApproved(int organizationId, int campaignId,int pageNumber, int pageSize);
    ResponseEntity<?> getListTransaction(int organizationUserId, int pageNumber, int pageSize);
    ResponseEntity<?> getListCampaignTransaction(Integer userId, int campaignId, int pageNumber, int pageSize);
    ResponseEntity<?> sendNotifyToMember(Principal connectedUser, NotificationDto notificationDto);
}
