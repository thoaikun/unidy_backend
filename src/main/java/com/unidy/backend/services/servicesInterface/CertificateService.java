package com.unidy.backend.services.servicesInterface;

import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface CertificateService {
    ResponseEntity<?> createCertificate(Principal connectedUser, int campaignId);

    ResponseEntity<?> getCertificate(Principal connectedUser, int campaignId);
}
