package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.Type.CampaignStatus;
import com.unidy.backend.domains.dto.requests.AuthenticationRequest;
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

    ResponseEntity<?> blockOrUnblockUser(int userid);

    ResponseEntity<?> getCampaignByStatus(CampaignStatus status, int skip, int limit);

    ResponseEntity<?> getCampaignPostByDate(Date fromDate, Date toDate, int skip, int limit);

    ResponseEntity<?> confirmSettlements(int settlementId, Principal userConnected);
}
