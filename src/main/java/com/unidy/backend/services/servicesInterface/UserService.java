package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.requests.ChangePasswordRequest;
import com.unidy.backend.domains.dto.requests.UserInformationRequest;
import com.unidy.backend.domains.dto.responses.UserInformationRespond;

import java.security.Principal;

public interface UserService {
    UserInformationRespond getUserInformation(UserInformationRequest request);
    void changePassword(ChangePasswordRequest request, Principal connectedUser);
}
