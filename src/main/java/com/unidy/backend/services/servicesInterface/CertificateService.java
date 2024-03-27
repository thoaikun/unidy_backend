package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.requests.CertificateRequest;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface CertificateService {
    ResponseEntity<?> createCertificate(Principal connectedUser, CertificateRequest certificateRequest);

    ResponseEntity<?> getCertificate(Principal connectedUser);
}
