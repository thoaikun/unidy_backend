package com.unidy.backend.controllers;


import com.unidy.backend.domains.dto.requests.MomoWebHookRequest;
import com.unidy.backend.domains.dto.responses.MomoResponse;
import com.unidy.backend.services.servicesInterface.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;


@RestController
@RequestMapping("/api/v1/donation")
@RequiredArgsConstructor
public class DonationController {
    private final DonationService donationService;

    @PostMapping("")
    public ResponseEntity<?> donation (Principal connectedUser, @RequestParam ("amount") Long amount,@RequestParam ("organizationUserId") int organizationUserId,@RequestParam ("campaignId") int campaignId) throws NoSuchAlgorithmException, InvalidKeyException {
        return donationService.executeTransaction(connectedUser,amount,organizationUserId,campaignId);
    }

    @PostMapping("/callBack-momo")
    public void callback_MOMO_IPN (@RequestBody MomoWebHookRequest momoResponse){
        try {
            donationService.handleTransaction(momoResponse);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
