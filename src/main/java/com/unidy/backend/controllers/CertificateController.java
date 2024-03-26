package com.unidy.backend.controllers;

import com.unidy.backend.domains.dto.requests.CertificateRequest;
import com.unidy.backend.services.servicesInterface.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/certificate")
@RequiredArgsConstructor
public class CertificateController {
    private final CertificateService certificateService;


    @PostMapping("")
    public ResponseEntity<?> createCertificate(Principal connectedUser, @RequestBody CertificateRequest certificateRequest){
        return certificateService.createCertificate(connectedUser,certificateRequest);
//        return ResponseEntity.ok().body("Đù má bị sao vậy ta");
    }
}
