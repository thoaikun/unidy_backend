package com.unidy.backend.services.servicesIplm;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.gson.Gson;
import com.unidy.backend.S3.S3Service;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.Type.NotificationType;
import com.unidy.backend.domains.dto.UserDto;
import com.unidy.backend.domains.dto.notification.NotificationDto;
import com.unidy.backend.domains.dto.notification.extraData.ExtraData;
import com.unidy.backend.domains.dto.notification.extraData.FriendAcceptData;
import com.unidy.backend.domains.dto.notification.extraData.FriendRequestData;
import com.unidy.backend.domains.dto.requests.ChoseFavoriteRequest;
import com.unidy.backend.domains.dto.responses.*;
import com.unidy.backend.domains.entity.*;
import com.unidy.backend.domains.entity.neo4j.UserNode;
import com.unidy.backend.domains.role.Role;
import com.unidy.backend.firebase.FirebaseService;
import com.unidy.backend.mapper.DtoMapper;
import com.unidy.backend.repositories.*;
import com.unidy.backend.domains.dto.requests.ChangePasswordRequest;
import com.unidy.backend.services.servicesInterface.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserServiceIplm implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final DtoMapper dtoMapper;
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final UserProfileImageRepository userProfileImageRepository;
    private final Neo4j_UserRepository neo4jUserRepository;
    private final FavoriteActivitiesRepository favoriteActivitiesRepository;
    private final TransactionRepository transactionRepository;
    private final VolunteerJoinCampaignRepository volunteerJoinCampaignRepository;
    private final FirebaseService firebaseService;
    private final Environment environment;
    private final NotificationRepository notificationRepository;

    public UserInformationRespond getUserInformation(Principal connectedUser){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        UserInformationRespond information = new UserInformationRespond() ;
        information.setUserId(user.getUserId());
        information.setFullName(user.getFullName());
        information.setAddress(user.getAddress());
        information.setSex(user.getSex());
        information.setPhone(user.getPhone());
        information.setDayOfBirth(user.getDayOfBirth());
        information.setJob(user.getJob());
        information.setRole(user.getRole());
        information.setWorkLocation(user.getWorkLocation());

        UserProfileImage image = userProfileImageRepository.findByUserId(user.getUserId());
        if (image != null){
            URL urlImage = s3Service.getObjectUrl(
                    "unidy",
                    "profile-images/%s/%s".formatted(user.getUserId(), image.getLinkImage())
            );
            information.setImage(urlImage.toString());
        }

        return information ;
    }

    @Override
    public ResponseEntity<?> getUserInformationByUserId(Principal connectedUser,int userId) {
        var userConnected = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            User user = userRepository.findByUserId(userId);

            if (user.getRole().equals(Role.VOLUNTEER)){
                UserInformationRespond information = new UserInformationRespond() ;
                RelationshipCheckResult checkRelationship = neo4jUserRepository.checkRelationship(userConnected.getUserId(),user.getUserId());
                information.setUserId(user.getUserId());
                information.setFullName(user.getFullName());
                information.setAddress(user.getAddress());
                information.setSex(user.getSex());
                information.setPhone(user.getPhone());
                information.setDayOfBirth(user.getDayOfBirth());
                information.setJob(user.getJob());
                information.setRole(user.getRole());
                information.setWorkLocation(user.getWorkLocation());
                information.setIsFriend(checkRelationship.isFriend());
                information.setIsRequested(checkRelationship.isRequested());
                information.setIsRequesting(checkRelationship.isRequesting());
                UserProfileImage image = userProfileImageRepository.findByUserId(user.getUserId());
                if (image != null){
                    URL urlImage = s3Service.getObjectUrl(
                            "unidy",
                            "profile-images/%s/%s".formatted(user.getUserId(), image.getLinkImage())
                    );
                    information.setImage(urlImage.toString());
                }
                return ResponseEntity.ok().body(information);
            }
            else {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("User is not volunteer"));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    public ResponseEntity<?> updateUserInformation(UserDto userDto,Principal connectedUser){
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            User userData = userRepository.findByUserId(user.getUserId());
            dtoMapper.updateUserInformation(userDto,userData);
            userRepository.save(userData);
            return ResponseEntity.ok().body(new SuccessReponse("Cập nhật thành công"));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Lỗi cập nhật thông tin"));
        }
    }


    public ResponseEntity<?> changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Sai mật khẩu"));
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Nhập lại khẩu mới không trùng khớp"));
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        userRepository.save(user);
        return ResponseEntity.ok().body(new SuccessReponse("Đổi mật khẩu thành công"));
    }


    public ResponseEntity<?> newPassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Nhập lại khẩu mới không trùng khớp"));
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok().body(new SuccessReponse("Đổi mật khẩu mới thành công"));
    }

    public ResponseEntity<?> updateProfileImage(MultipartFile imageFile, Principal connectedUser){
        String linkS3 = environment.getProperty("LINK_S3");
        String profileImageId = UUID.randomUUID().toString();
        String fileContentType = imageFile.getContentType();
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        int userId = user.getUserId();

        try {
            if (fileContentType != null &&
                    (fileContentType.equals("image/png") ||
                            fileContentType.equals("image/jpeg") ||
                            fileContentType.equals("image/jpg"))) {
                fileContentType = fileContentType.replace("image/",".");
                s3Service.putObject(
                        "unidy",
                        fileContentType,
                        "profile-images/%s/%s".formatted(userId, profileImageId+fileContentType ),
                        imageFile.getBytes()
                );

                UserProfileImage image = userProfileImageRepository.findByUserId(userId);
                if (image == null){
                    image = new UserProfileImage();
                }
                image.setLinkImage(profileImageId+fileContentType);
                image.setUpdateDate(new Date());
                image.setUserId(userId);
                userProfileImageRepository.save(image);

                String imageUrl = linkS3 + "profile-images/" + userId + "/" + profileImageId + fileContentType;
                UserNode userNode = neo4jUserRepository.findUserNodeByUserId(userId);
                userNode.setProfileImageLink(imageUrl);
                neo4jUserRepository.save(userNode);
                return ResponseEntity.ok().body(new SuccessReponse(imageUrl));

            } else {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Unsupported file format"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    public ResponseEntity<?> addFriend(Principal connectedUser, int friendId){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            if (neo4jUserRepository.checkRelationship(user.getUserId(), friendId).isRequesting()) {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Allready send invite"));
            }
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            neo4jUserRepository.friendInviteRequest(user.getUserId(), friendId, sdf.format(date).toString());

            User friend = userRepository.findByUserId(friendId);
            ExtraData extraData = new FriendRequestData(user.getUserId(), user.getFullName());
            ArrayList<String> deviceTokens = new ArrayList<>();
            for (UserDeviceFcmToken deviceToken : friend.getUserDeviceFcmTokens()) {
                deviceTokens.add(deviceToken.getFcmToken());
            }

            if (!deviceTokens.isEmpty()) {
                NotificationDto notification = NotificationDto.builder()
                        .title("Lời mời kết bạn")
                        .body("%s đã gửi lời mời kết bạn".formatted(user.getFullName()))
                        .deviceTokens(deviceTokens)
                        .extraData(extraData)
                        .build();
                firebaseService.pushNotificationToMultipleDevices(notification);
            }
            Map<String, String> extra = new HashMap<>();
            extra.put("id", String.valueOf(user.getUserId()));
            firebaseService.saveNotification(user.getUserId(), friendId, NotificationType.FRIEND_REQUEST,
                    "Lời mời kết bạn", "%s đã gửi lời mời kết bạn".formatted(user.getFullName()),
                    new Gson().toJson(extra));

            return ResponseEntity.ok().body(new SuccessReponse("Send invite success"));
        } catch (FirebaseMessagingException error) {
            System.out.println(error.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Cannot send friend request"));
        }

        return ResponseEntity.badRequest().body(new ErrorResponseDto("Cannot send friend request"));
    }

    public ResponseEntity<?> acceptFriendInvite(Principal connectedUser, int friendId){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            if (neo4jUserRepository.checkRelationship(user.getUserId(), friendId).isRequested()){
                neo4jUserRepository.declineInviteRequest(user.getUserId(),friendId);
                neo4jUserRepository.createFriendship(user.getUserId(),friendId);

                User requestUser = userRepository.findByUserId(friendId);
                ExtraData extraData = new FriendAcceptData(user.getUserId(), user.getFullName());
                ArrayList<String> deviceTokens = new ArrayList<>();
                for (UserDeviceFcmToken deviceToken : requestUser.getUserDeviceFcmTokens()) {
                    deviceTokens.add(deviceToken.getFcmToken());
                }

                if (!deviceTokens.isEmpty()) {
                    NotificationDto notification = NotificationDto.builder()
                            .title("Kết bạn thành công")
                            .body("%s đã chấp nhận lời mời kết bạn".formatted(user.getFullName()))
                            .deviceTokens(deviceTokens)
                            .extraData(extraData)
                            .build();
                    firebaseService.pushNotificationToMultipleDevices(notification);
                }
                Map<String, String> extra = new HashMap<>();
                extra.put("id", String.valueOf(user.getUserId()));
                firebaseService.saveNotification(user.getUserId(), friendId, NotificationType.FRIEND_ACCEPT,
                        "Kết bạn thành công", "%s đã chấp nhận lời mời kết bạn".formatted(user.getFullName()),
                        new Gson().toJson(extra));

                return ResponseEntity.ok().body(new SuccessReponse("Accept invite success"));
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Must have invite yet"));
            }
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something error"));
        }
    }

    public ResponseEntity<?> unfriend(Principal connectedUser, int friendId) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            neo4jUserRepository.unfriend(user.getUserId(), friendId);
            return ResponseEntity.ok().body(new SuccessReponse("Unfriend success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something error"));
        }
    }

    public ResponseEntity<?> getListInvite(Principal connectedUser, int skip, int limit){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            List<InviteFriend> listInvite = neo4jUserRepository.getListInvite(user.getUserId(),skip,limit);
            return ResponseEntity.ok().body(listInvite);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something error"));
        }
    }

    public ResponseEntity<?> declineInvite(Principal connectedUser, int friendId){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
             neo4jUserRepository.declineInviteRequest(user.getUserId(),friendId);
            return ResponseEntity.ok().body("Decline success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something error"));
        }
    }

    public ResponseEntity<?> getRecommendFriend(Principal connectedUser, int skip, int limit, int rangeEnd){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            List<RecommendFriendResponse> recommendFriend = neo4jUserRepository.getRecommendFriend(user.getUserId(), limit, skip, rangeEnd);
            return ResponseEntity.ok().body(recommendFriend);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    public ResponseEntity<?> getListFriend(Principal connectedUser, int limit, int skip){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            List<UserNode> recommendFriend = neo4jUserRepository.getListFriend(user.getUserId(), limit, skip);
            return ResponseEntity.ok().body(recommendFriend);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> deleteInvite(Principal connectedUser, int friendId) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            if (neo4jUserRepository.checkRelationship(user.getUserId(),friendId).isRequesting()){
                neo4jUserRepository.deleteInvite(user.getUserId(),friendId);
                return ResponseEntity.ok().body("Delete success");
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Invite not exist"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something error"));
        }
    }

    public ResponseEntity<?> choseFavoriteActivities(Principal connectedUser, ChoseFavoriteRequest request){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            FavoriteActivities favorite = FavoriteActivities
                    .builder()
                    .userId(user.getUserId())
                    .communityType(request.getCommunity_type())
                    .education(request.getEducation_type())
                    .environment(request.getEnvironment())
                    .emergencyPreparedness(request.getEmergency_preparedness())
                    .helpOther(request.getHelp_other())
                    .healthy(request.getHealthy())
                    .research(request.getResearch_writing_editing())
                    .build();
            favoriteActivitiesRepository.save(favorite);
            return ResponseEntity.ok().body(new SuccessReponse("Success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    public ResponseEntity<?> followOrganization(Principal connectedUser, int organizationId){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            if (neo4jUserRepository.checkRelationship(user.getUserId(), organizationId).isFollowed()) {
                return ResponseEntity.badRequest().body(new FollowOrganizationResponse("Already followed", null));
            }
            Optional<Organization> followedOrganization = this.organizationRepository.findByUserId(organizationId);
            if (followedOrganization.isEmpty()) {
                return ResponseEntity.badRequest().body(new FollowOrganizationResponse(
                        "Follow failed",
                        null
                ));
            }

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            neo4jUserRepository.sendFollowRequest(user.getUserId(), organizationId, sdf.format(date));

            // return organization firebase topic for device to subscribe
            return ResponseEntity.ok().body(new FollowOrganizationResponse(
                    "Follow success",
                    followedOrganization.get().getFirebaseTopic()
            ));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something error"));
        }
    }
    @Override
    public ResponseEntity<?> getUserTransaction(Principal connectedUser, int pageSize, int pageNumber) {
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("transactionId").descending());
            List<TransactionResponse> transactionResponses = transactionRepository.findTransactionByUserId(user.getUserId(), pageable);
            return ResponseEntity.ok().body(transactionResponses);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> getUserJoinedCampaigns(Principal connectedUser, int pageSize, int pageNumber) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            List<VolunteerJoinCampaign> volunteerJoinCampaigns = volunteerJoinCampaignRepository.findVolunteerJoinCampaignByUserId(user.getUserId(), pageable);
            return ResponseEntity.ok().body(volunteerJoinCampaigns);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<UserNode>> searchUser(Principal connectedUser, String searchTerm, int limit, int skip){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return CompletableFuture.completedFuture(neo4jUserRepository.searchUser(user.getUserId(), searchTerm, limit, skip));
    }

    public ResponseEntity<?> getNotification(Principal connectedUser, int pageSize, int pageNumber) {
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdTime").descending());
            List<NotificationResponse> notificationResponses = notificationRepository.getNotificationsByReceiverId(user.getUserId(), pageable);
            return ResponseEntity.ok().body(notificationResponses);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    public ResponseEntity<?> getUnseenNotification(Principal connectedUser) {
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            Integer unseenNotificationCount = notificationRepository.countByReceiverIdAndSeenTimeIsNull(user.getUserId());
            return ResponseEntity.ok().body(new NotificationResponse.UnseenCountResponse(unseenNotificationCount));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    public ResponseEntity<?> markAsSeen(Principal connectedUser, int notificationId) {
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            Notification notification = notificationRepository.findById(notificationId).orElse(null);
            if (notification == null) {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Không tìm thấy thông báo"));
            }
            if (notification.getReceiverId() != user.getUserId()) {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Không thể đánh dấu thông báo của người khác"));
            }
            notification.setSeenTime(new Timestamp(System.currentTimeMillis()));
            notificationRepository.save(notification);
            return ResponseEntity.ok().body(new SuccessReponse("Đã đánh dấu thông báo"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    public ResponseEntity<?> markAllAsSeen(Principal connectedUser) {
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            List<Notification> notifications = notificationRepository.getNotificationsByReceiverIdAndSeenTimeIsNull(user.getUserId());
            for (Notification notification : notifications) {
                notification.setSeenTime(new Timestamp(System.currentTimeMillis()));
            }
            notificationRepository.saveAll(notifications);
            return ResponseEntity.ok().body(new SuccessReponse("Đã đánh dấu tất cả thông báo"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }
}
