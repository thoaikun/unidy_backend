package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.requests.CampaignRequest;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface CampaignService {
    ResponseEntity<?> createCampaign(Principal userConnected, CampaignRequest request);
}
