package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.requests.MomoWebHookRequest;
import com.unidy.backend.domains.dto.responses.MomoResponse;
import org.springframework.http.ResponseEntity;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

public interface DonationService {
    ResponseEntity<?> executeTransaction (Principal connectedUser, Long totalAmount, int organizationUserId, int campaignId) throws NoSuchAlgorithmException, InvalidKeyException;

    void handleTransaction(MomoWebHookRequest momoResponse) throws NoSuchAlgorithmException, InvalidKeyException;
}
