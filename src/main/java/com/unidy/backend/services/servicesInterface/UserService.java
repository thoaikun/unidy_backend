package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.UserDto;
import com.unidy.backend.domains.dto.requests.ChangePasswordRequest;
import com.unidy.backend.domains.dto.requests.UserInformationRequest;
import com.unidy.backend.domains.dto.responses.UserInformationRespond;
import com.unidy.backend.domains.entity.User;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface UserService {
    UserInformationRespond getUserInformation(int userId);
    ResponseEntity<?> updateUserInformation(UserDto userDto);
    void changePassword(ChangePasswordRequest request, Principal connectedUser);
}
