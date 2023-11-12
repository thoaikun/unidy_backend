package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.UserDto;
import org.springframework.http.ResponseEntity;

public interface PostService {
    public ResponseEntity<?> getPost(UserDto request);
}
