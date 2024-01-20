package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.UserDto;
import com.unidy.backend.domains.dto.requests.ChangePasswordRequest;
import com.unidy.backend.domains.dto.requests.UserInformationRequest;
import com.unidy.backend.domains.dto.responses.UserInformationRespond;
import com.unidy.backend.domains.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

public interface UserService {
    UserInformationRespond getUserInformation(Principal connectedUser);
    ResponseEntity<?> updateUserInformation(UserDto userDto,Principal connectedUser);
    ResponseEntity<?>  changePassword(ChangePasswordRequest request, Principal connectedUser);
    ResponseEntity<?>  newPassword(ChangePasswordRequest request, Principal connectedUser);
    ResponseEntity<?> updateProfileImage(MultipartFile imageFile, Principal connectedUser);
    ResponseEntity<?> addFriend(Principal connectedUser, int friendId);
    ResponseEntity<?> acceptFriendInvite(Principal connectedUser, int friendId);
    ResponseEntity<?> unfriend(Principal connectedUser, int friendId);
    ResponseEntity<?> getListInvite(Principal connectedUser);
    ResponseEntity<?> deleteInvite(Principal connectedUser, int friendId);

    ResponseEntity<?> getRecommendFriend(Principal connectedUser, int skip, int limit, int rangeEnd);
}
