package com.unidy.backend.services.servicesInterface;

import org.springframework.http.ResponseEntity;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

public interface DonationService {
    ResponseEntity<?> executeTransaction (Principal connectedUser, Long totalAmount) throws NoSuchAlgorithmException, InvalidKeyException;
}
