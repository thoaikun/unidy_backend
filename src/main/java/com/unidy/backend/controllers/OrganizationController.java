package com.unidy.backend.controllers;

import com.unidy.backend.services.servicesInterface.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor

@PreAuthorize("hasRole('ORGANIZATION')")
@RequestMapping("/api/v1/organization")
public class OrganizationController {
    private OrganizationService organizationService;

    @PostMapping("/get-profile")
    public ResponseEntity<?> getProfile(@RequestParam int organizationId){
       return organizationService.getProfileOrganization(organizationId);
    }

}
