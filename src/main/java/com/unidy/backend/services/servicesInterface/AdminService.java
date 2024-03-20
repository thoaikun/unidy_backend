package com.unidy.backend.services.servicesInterface;

import org.springframework.http.ResponseEntity;

public interface AdminService {
    ResponseEntity<?> runOrStopJob(int jobId);
}
