package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.Type.CampaignStatus;
import com.unidy.backend.domains.dto.requests.AuthenticationRequest;
import com.unidy.backend.domains.dto.requests.PostCondition;
import com.unidy.backend.domains.dto.requests.RegisterRequest;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Date;

public interface AdminService {
    ResponseEntity<?> runOrStopJob(int jobId);

    ResponseEntity<?> blockOrUnblockPost(String postId, String status);

    ResponseEntity<?> deletePost(String postId);

    ResponseEntity<?> getPostByDate(Date fromDate, Date toDate, int pageNumber, int pageSize);

    ResponseEntity<?> authenticate(AuthenticationRequest request);

    ResponseEntity<?> register(RegisterRequest request);

    ResponseEntity<?> approveOrganization(int organizationId);

    ResponseEntity<?> getAllVolunteers(int pageNumber, int pageSize);

    ResponseEntity<?> getAllOrganizations(int pageNumber, int pageSize);

    ResponseEntity<?> blockOrUnblockUser(int userid);

    ResponseEntity<?> confirmSettlements(int settlementId, Principal userConnected);

    ResponseEntity<?> getCampaign(PostCondition postCondition);

    ResponseEntity<?> getCampaignByCampaignId(Integer campaignId);

    ResponseEntity<?> getTransactionByCampaignId(Integer campaignId, int pageNumber, int pageSize);

    ResponseEntity<?> getApprovedVolunteers(int organizationId, int campaignId, int pageNumber, int pageSize);

    ResponseEntity<?> getNotApprovedVolunteers(int organizationId, int campaignId, int pageNumber, int pageSize);

    ResponseEntity<?> getNotApprovedOrganizations(int pageNumber, int pageSize);

    ResponseEntity<?> getVolunteerByVolunteerId(int volunteerId);

    ResponseEntity<?> getOrganizationInformation(int organizationId);

    ResponseEntity<?> getPostByPostId(String postId);
}
