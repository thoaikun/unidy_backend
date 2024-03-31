package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.UserDto;
import com.unidy.backend.domains.dto.requests.ChangePasswordRequest;
import com.unidy.backend.domains.dto.requests.ChoseFavoriteRequest;
import com.unidy.backend.domains.dto.responses.UserInformationRespond;
import com.unidy.backend.domains.entity.neo4j.UserNode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserService {
    UserInformationRespond getUserInformation(Principal connectedUser);
    ResponseEntity<?> getUserInformationByUserId(Principal connectedUser,int userId);
    ResponseEntity<?> updateUserInformation(UserDto userDto,Principal connectedUser);
    ResponseEntity<?>  changePassword(ChangePasswordRequest request, Principal connectedUser);
    ResponseEntity<?>  newPassword(ChangePasswordRequest request, Principal connectedUser);
    ResponseEntity<?> updateProfileImage(MultipartFile imageFile, Principal connectedUser);
    ResponseEntity<?> addFriend(Principal connectedUser, int friendId);
    ResponseEntity<?> acceptFriendInvite(Principal connectedUser, int friendId);
    ResponseEntity<?> unfriend(Principal connectedUser, int friendId);
    ResponseEntity<?> getListInvite(Principal connectedUser, int skip, int limit);
    ResponseEntity<?> declineInvite(Principal connectedUser, int friendId);
    ResponseEntity<?> getRecommendFriend(Principal connectedUser, int skip, int limit, int rangeEnd);
    ResponseEntity<?> getListFriend(Principal connectedUser, int limit, int skip);
    ResponseEntity<?> deleteInvite(Principal connectedUser, int friendId);
    ResponseEntity<?> choseFavoriteActivities(Principal connectedUser, ChoseFavoriteRequest choseFavoriteRequest);
    ResponseEntity<?> followOrganization(Principal connectedUser, int organizationId);
    ResponseEntity<?> getUserTransaction(Principal connectedUser, int pageSize, int pageNumber);
    ResponseEntity<?> getUserJoinedCampaigns(Principal connectedUser, int pageSize, int pageNumber);
    @Async("threadPoolTaskExecutor")
    CompletableFuture<List<UserNode>> searchUser(Principal connectedUser, String searchTerm, int limit, int skip);
}
