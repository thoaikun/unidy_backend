package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.S3.S3Service;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.dto.notification.NotificationDto;
import com.unidy.backend.domains.dto.responses.*;
import com.unidy.backend.domains.entity.*;
import com.unidy.backend.firebase.FirebaseService;
import com.unidy.backend.repositories.*;
import com.unidy.backend.services.servicesInterface.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganizationServiceIplm implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final TransactionRepository transactionRepository;
    private final VolunteerJoinCampaignRepository volunteerJoinCampaignRepository;
    private final CampaignRepository campaignRepository;
    private final FirebaseService firebaseService;
    private final UserProfileImageRepository userProfileImageRepository;
    private final Neo4j_UserRepository neo4j_UserRepository;
    private final S3Service s3Service;

    @Override
    public ResponseEntity<?> getProfileOrganization(Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            OrganizationInformation organizationInformation = organizationRepository.getOrganizationInformation(user.getUserId());
            if (organizationInformation.getImage() != null){
                URL urlImage = s3Service.getObjectUrl(
                        "unidy",
                        "profile-images/%s/%s".formatted(organizationInformation.getUserId(), organizationInformation.getImage())
                );
                organizationInformation.setImage(urlImage.toString());
            }
            Integer totalCampaign = campaignRepository.countCampaignByOwner(user.getUserId());
            Integer totalVolunteer = volunteerJoinCampaignRepository.countVolunteerByOrganizationId(user.getUserId());
            Integer totalAmountTransaction = transactionRepository.sumAmountTransactionByOrganizationUserId(user.getUserId());
            Integer totalAmountTransactionInDay = transactionRepository.sumAmountTransactionByOrganizationUserIdInDay(user.getUserId());
            OrganizationInformation.OverallFigure overallFigure = new OrganizationInformation.OverallFigure(
                totalCampaign,
                totalVolunteer,
                totalAmountTransaction,
                totalAmountTransactionInDay
            );
            organizationInformation.setOverallFigure(overallFigure);
            return ResponseEntity.ok().body(organizationInformation);
        } catch (Exception exception){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something Error"));
        }
    }

    public ResponseEntity<?> getProfileOrganization(Principal connectedUser,  int organizationId){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            OrganizationInformation organizationInformation = new OrganizationInformation();
            organizationInformation.setUserId(organizationId);
            Optional<Organization> organization = organizationRepository.findByUserId(organizationId);
            if (organization.isEmpty()){
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Organization not found"));
            }

            CheckResult checkFollow = neo4j_UserRepository.checkFollow(user.getUserId(), organizationId);
            organizationInformation.setOrganizationName(organization.get().getOrganizationName());
            organizationInformation.setIsFollow(checkFollow.isResult());
            organizationInformation.setEmail(organization.get().getEmail());
            organizationInformation.setAddress(organization.get().getAddress());
            organizationInformation.setCountry(organization.get().getCountry());
            organizationInformation.setPhone(organization.get().getPhone());
            organizationInformation.setFirebaseTopic(organization.get().getFirebaseTopic());
            UserProfileImage image = userProfileImageRepository.findByUserId(organizationId);
            if (image != null){
                URL urlImage = s3Service.getObjectUrl(
                        "unidy",
                        "profile-images/%s/%s".formatted(organizationId, image.getLinkImage())
                );
                organizationInformation.setImage(urlImage.toString());
            }
            return ResponseEntity.ok().body(organizationInformation);
        } catch (Exception exception){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something Error"));
        }
    }

    @Override
    public ResponseEntity<?> getListVolunteer(Principal connectedUser, int campaignId) {
        try {
            List<VolunteerJoinResponse> volunteerJoinCampaigns = volunteerJoinCampaignRepository.findVolunteerJoinCampaignByCampaignId(campaignId);
            return ResponseEntity.ok().body(volunteerJoinCampaigns);
        } catch (Exception exception){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something Error"));
        }
    }

    public ResponseEntity<?> getListVolunteerNotApproved(int organizationId, int campaignId, int pageNumber, int pageSize){
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            List<ListVolunteerResponse> listVolunteerNotApproved = organizationRepository.getListVolunteerNotApproved(organizationId,campaignId,pageable);
            return ResponseEntity.ok().body(listVolunteerNotApproved);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    public ResponseEntity<?> approveVolunteer(Principal connectedUser, int campaignId, List<Integer> volunteerIds){
        if (volunteerIds.isEmpty())
            return ResponseEntity.badRequest().body(new ErrorResponseDto("VolunteerIds is empty"));

        var organization = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            List<VolunteerJoinCampaign> volunteerJoinCampaigns = volunteerJoinCampaignRepository.findVolunteerJoinCampaignByCampaignIdAndUserIdIn(campaignId, volunteerIds);
            Campaign campaign = campaignRepository.findCampaignByCampaignId(campaignId);

            if (!organization.getUserId().equals(campaign.getOwner()))
                return ResponseEntity.badRequest().body(new ErrorResponseDto("CampaignId không thuộc quyền sở hữu của bạn"));
            if (volunteerJoinCampaigns.isEmpty())
                return ResponseEntity.badRequest().body(new ErrorResponseDto("VolunteerIds không tồn tại"));
            else if (campaign.getNumberVolunteerRegistered() + volunteerIds.size() > campaign.getNumberVolunteer())
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Số lượng tình nguyện viên vượt quá số lượng cho phép"));

            volunteerJoinCampaignRepository.approveVolunteerJoinCampaignByCampaignIdAndUserIdIn(campaignId, volunteerIds);
            return ResponseEntity.ok().body(new SuccessReponse("Approve volunteer success"));

        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something error"));
        }
    }

    @Override
    public ResponseEntity<?> rejectVolunteer(Principal connectedUser, int campaignId, List<Integer> volunteerIds) {
        if (volunteerIds.isEmpty())
            return ResponseEntity.badRequest().body(new ErrorResponseDto("VolunteerIds is empty"));

        var organization = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            List<VolunteerJoinCampaign> volunteerJoinCampaigns = volunteerJoinCampaignRepository.findVolunteerJoinCampaignByCampaignIdAndUserIdIn(campaignId, volunteerIds);
            Campaign campaign = campaignRepository.findCampaignByCampaignId(campaignId);

            if (!organization.getUserId().equals(campaign.getOwner()))
                return ResponseEntity.badRequest().body(new ErrorResponseDto("CampaignId không thuộc quyền sở hữu của bạn"));
            if (volunteerJoinCampaigns.isEmpty())
                return ResponseEntity.badRequest().body(new ErrorResponseDto("VolunteerIds không tồn tại"));

            volunteerJoinCampaignRepository.rejectVolunteerJoinCampaignByCampaignIdAndUserIdIn(campaignId, volunteerIds);
            return ResponseEntity.ok().body(new SuccessReponse("Reject volunteer success"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something error"));
        }
    }

    public ResponseEntity<?> getListVolunteerApproved(int organizationId, int campaignId, int pageNumber, int pageSize){
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            List<ListVolunteerResponse> listVolunteerApproved = organizationRepository.getListVolunteerApproved(organizationId,campaignId,pageable);
            return ResponseEntity.ok().body(listVolunteerApproved);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }
    public ResponseEntity<?> getListTransaction(int organizationUserId, int pageNumber, int pageSize, String sort){
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            List<TransactionResponse> transaction;
            if (sort == null || sort.equals("newest")) {
                transaction = transactionRepository.findTransactionsByOrganizationUserIdSortByDate(organizationUserId, pageable);
            }
            else {
                transaction = transactionRepository.findTransactionsByOrganizationUserIdSortByTransactionAmount(organizationUserId, pageable);
            }
            return ResponseEntity.ok().body(transaction);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }
    @Override
    public ResponseEntity<?> getListCampaignTransaction(Integer organizationUserId, int campaignId, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("transactionTime").descending());
            List<TransactionResponse> transaction = transactionRepository.findTransactionsByOrganizationUserIdAndCampaignId(organizationUserId,campaignId, pageable);
            return ResponseEntity.ok().body(transaction);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> sendNotifyToMember(Principal connectedUser, NotificationDto notificationDto) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
//            List<UserNode> userFollow = neo4j_userRepository.findUserFollowOrganization(user.getUserId());
////            ExtraData extraData = new NewCampaignData(campaignId, organization.get().getOrganizationId(), request.getTitle());
//            NotificationDto notification = NotificationDto.builder()
//                    .title(organization.getOrganizationName() + " tổ chức chiến dịch mới")
//                    .body(request.getDescription())
//                    .topic(organization.get().getFirebaseTopic())
//                    .extraData("extraData")
//                    .build();
//            firebaseService.pushNotificationToTopic(notification);
            return null;
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }
}
