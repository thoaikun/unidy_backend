package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.UserDto;
import com.unidy.backend.domains.dto.requests.UserInformationRequest;
import org.springframework.http.ResponseEntity;

public interface PostService {
    public ResponseEntity<?> getPost(UserInformationRequest request);
}
