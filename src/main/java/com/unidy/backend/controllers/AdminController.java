package com.unidy.backend.controllers;

import com.unidy.backend.services.servicesInterface.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
//@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    @GetMapping("/runOrStopJob")
    public ResponseEntity<?> runOrStopScheduleJob(@RequestParam("jobId") int jobId){
        return adminService.runOrStopJob(jobId);
    }
}
