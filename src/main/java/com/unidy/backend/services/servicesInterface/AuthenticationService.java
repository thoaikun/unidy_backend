package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.requests.AuthenticationRequest;
import com.unidy.backend.domains.dto.requests.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface AuthenticationService {
     ResponseEntity<?> register(RegisterRequest request);
     ResponseEntity<?> authenticate(AuthenticationRequest request);
     void refreshToken(HttpServletRequest request, HttpServletResponse response)throws IOException;
}
