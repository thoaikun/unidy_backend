package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.S3.S3Service;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.Type.CampaignStatus;
import com.unidy.backend.domains.Type.VolunteerStatus;
import com.unidy.backend.domains.dto.notification.NotificationDto;
import com.unidy.backend.domains.dto.notification.extraData.CertificateData;
import com.unidy.backend.domains.dto.notification.extraData.ExtraData;
import com.unidy.backend.domains.dto.requests.CampaignDto;
import com.unidy.backend.domains.dto.responses.*;
import com.unidy.backend.domains.entity.*;
import com.unidy.backend.firebase.FirebaseService;
import com.unidy.backend.repositories.*;
import com.unidy.backend.services.servicesInterface.CertificateService;
import com.unidy.backend.services.servicesInterface.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;

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
    private final CertificateService certificateService;
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final UserDeviceFcmTokenRepository userDeviceFcmTokenRepository;
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

    public ResponseEntity<?> approveVolunteer(Principal connectedUser, int volunteerId, int campaignId){
        var organization = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            VolunteerJoinCampaign volunteerJoinCampaign = volunteerJoinCampaignRepository.findVolunteerJoinCampaignByUserIdAndCampaignId(volunteerId,campaignId);
            Campaign campaign = campaignRepository.findCampaignByCampaignId(campaignId);
            if (organization.getUserId().equals(campaign.getOwner()) && campaign.getNumberVolunteerRegistered() < campaign.getNumberVolunteer() ){
                volunteerJoinCampaign.setStatus(String.valueOf(VolunteerStatus.APPROVED));
                volunteerJoinCampaignRepository.save(volunteerJoinCampaign);
                return ResponseEntity.ok().body("Approve success");
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Not permission"));
            }
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
    public ResponseEntity<?> getListTransaction(int organizationUserId, int pageNumber, int pageSize){
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("transactionTime").descending());
            List<TransactionResponse> transaction = transactionRepository.findTransactionsByOrganizationUserId(organizationUserId, pageable);
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

    @Override
    public ResponseEntity<?> endCampaign(Principal connectedUser, int campaignId) {
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            Optional<Organization> organization = organizationRepository.findByUserId(user.getUserId());
            Campaign campaign = campaignRepository.findCampaignByCampaignId(campaignId);
            campaign.setStatus(CampaignStatus.COMPLETE.toString());
            certificateService.createCertificate(connectedUser,campaignId);
            List<VolunteerJoinCampaign> volunteerJoinCampaigns = volunteerJoinCampaignRepository.findUserIdsByCampaignIdAndStatus(campaignId, "APPROVE");
            for (VolunteerJoinCampaign volunteerJoinCampaign : volunteerJoinCampaigns) {
                certificateRepository.findCertificate(volunteerJoinCampaign.getUserId());
                User volunteer = userRepository.findByUserId(volunteerJoinCampaign.getUserId());
                Certificate certificate = certificateRepository.findCertificateByUserId(volunteer.getUserId(),campaignId);
                ExtraData extraData = new CertificateData(
                        campaign.getCampaignId(),
                        campaign.getDescription(),
                        user.getUserId(),
                        organization.get().getOrganizationName(),
                        volunteer.getFullName(),
                        certificate.getFile()
                );
                List<UserDeviceFcmToken> userDeviceFcmToken = userDeviceFcmTokenRepository.findByUserId(volunteer.getUserId());
                ArrayList<String> listFCMToken = new ArrayList<>();
                for (UserDeviceFcmToken token : userDeviceFcmToken){
                    listFCMToken.add(token.getFcmToken());
                }
                NotificationDto notificationDto =  NotificationDto.builder()
                        .title("Sự kiện " + campaign.getDescription() + " đã kết thúc.")
                        .body(organization.get().getOrganizationName() + " gửi tới bạn chứng nhận tham gia chiến dịch")
                        .deviceTokens(listFCMToken)
                        .extraData(extraData)
                        .build();

                firebaseService.pushNotificationToMultipleDevices(notificationDto);
            }

//            List<Transaction> listTransaction = transactionRepository.findTransactionsByCampaignId(campaignId);
//            for (Transaction transaction : listTransaction){
//                certificateRepository.findCertificate(volunteerJoinCampaign.getUserId());
//            }
            return ResponseEntity.ok().body(new SuccessReponse("End campaign success"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> updateCampaignInformation(Principal connectedUser, int campaignId, CampaignDto campaignDto) {
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            Campaign campaign = campaignRepository.findCampaignByCampaignId(campaignId);
            if (campaign == null) {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Can't find campaign"));
            }
            if (!user.getUserId().equals(campaign.getOwner())){
                return ResponseEntity.badRequest().body(new ErrorResponseDto("You can't update this campaign"));
            }

            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration()
                    .setMatchingStrategy(MatchingStrategies.STRICT)
                    .setSkipNullEnabled(true);
            modelMapper.map(campaignDto, campaign);
            campaign.setUpdateDate(new Date());
            campaign.setUpdateBy(user.getUserId());
            campaignRepository.save(campaign);

            return ResponseEntity.ok().body(new SuccessReponse("Campaign information updated successfully."));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }
}
