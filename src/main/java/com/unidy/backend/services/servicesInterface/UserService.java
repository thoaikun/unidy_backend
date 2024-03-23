package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.UserDto;
import com.unidy.backend.domains.dto.requests.ChangePasswordRequest;
import com.unidy.backend.domains.dto.requests.ChoseFavoriteRequest;
import com.unidy.backend.domains.dto.requests.UserInformationRequest;
import com.unidy.backend.domains.dto.responses.UserInformationRespond;
import com.unidy.backend.domains.entity.User;
import com.unidy.backend.domains.entity.neo4j.UserNode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserService {
    UserInformationRespond getUserInformation(Principal connectedUser);
    UserInformationRespond getUserInformationByUserId(int userId);
    ResponseEntity<?> updateUserInformation(UserDto userDto,Principal connectedUser);
    ResponseEntity<?>  changePassword(ChangePasswordRequest request, Principal connectedUser);
    ResponseEntity<?>  newPassword(ChangePasswordRequest request, Principal connectedUser);
    ResponseEntity<?> updateProfileImage(MultipartFile imageFile, Principal connectedUser);
    ResponseEntity<?> addFriend(Principal connectedUser, int friendId);
    ResponseEntity<?> acceptFriendInvite(Principal connectedUser, int friendId);
    ResponseEntity<?> unfriend(Principal connectedUser, int friendId);
    ResponseEntity<?> getListInvite(Principal connectedUser, String cursor, int limit);
    ResponseEntity<?> deleteInvite(Principal connectedUser, int friendId);
    ResponseEntity<?> getRecommendFriend(Principal connectedUser, int skip, int limit, int rangeEnd);
    ResponseEntity<?> getListFriend(Principal connectedUser, int limit, int cursor);
    ResponseEntity<?> choseFavoriteActivities(Principal connectedUser, ChoseFavoriteRequest choseFavoriteRequest);
    ResponseEntity<?> followOrganization(Principal connectedUser, int organizationId);
    ResponseEntity<?> getUserTransaction(Principal connectedUser, int limit, int offset);

    ResponseEntity<?> getUserJoinedCampaigns(Principal connectedUser, int limit, int offset);

    @Async("threadPoolTaskExecutor")
    CompletableFuture<List<UserNode>> searchUser(String searchTerm, int limit, int skip);
}
