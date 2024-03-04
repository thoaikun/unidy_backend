package com.unidy.backend.controllers;


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

    @GetMapping("")
    public ResponseEntity<?> donation (Principal connectedUser, @RequestParam ("amount") Long amount) throws NoSuchAlgorithmException, InvalidKeyException {
        return donationService.executeTransaction(connectedUser,amount);
    }

    @PostMapping("/callBack-momo")
    public void callback_MOMO_IPN (@RequestBody MomoResponse momoResponse){
        try {
            donationService.handleTransaction(momoResponse);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
