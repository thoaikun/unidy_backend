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

    ResponseEntity<?> blockOrUnblockUser(int userid);

    ResponseEntity<?> confirmSettlements(int settlementId, Principal userConnected);

    ResponseEntity<?> getCampaign(PostCondition postCondition);
}
