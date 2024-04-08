package com.unidy.backend.services.servicesIplm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidy.backend.S3.S3Service;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.Type.CampaignStatus;
import com.unidy.backend.domains.dto.notification.NotificationDto;
import com.unidy.backend.domains.dto.notification.extraData.CertificateData;
import com.unidy.backend.domains.dto.notification.extraData.ExtraData;
import com.unidy.backend.domains.dto.requests.CampaignDto;
import com.unidy.backend.domains.dto.responses.*;
import com.unidy.backend.domains.entity.*;
import com.unidy.backend.domains.entity.neo4j.CampaignNode;
import com.unidy.backend.domains.entity.relationship.CampaignType;
import com.unidy.backend.firebase.FirebaseService;
import com.unidy.backend.repositories.*;
import com.unidy.backend.services.servicesInterface.CertificateService;
import com.unidy.backend.services.servicesInterface.OrganizationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganizationServiceIplm implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final TransactionRepository transactionRepository;
    private final VolunteerJoinCampaignRepository volunteerJoinCampaignRepository;
    private final CampaignRepository campaignRepository;
    private final UserProfileImageRepository userProfileImageRepository;
    private final Neo4j_UserRepository neo4j_UserRepository;
    private final Neo4j_CampaignRepository neo4j_CampaignRepository;
    private final UserDeviceFcmTokenRepository userDeviceFcmTokenRepository;
    private final CertificateRepository certificateRepository;
    private final CampaignTypeRepository campaignTypeRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final FirebaseService firebaseService;
    private final CertificateService certificateService;
    private final SettlementRepository settlementRepository;

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

    public ResponseEntity<?> getListCampaign(int organizationId, int pageNumber, int pageSize){
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            List<Campaign> campaignResponses = campaignRepository.getCampaignsByOwner(organizationId, pageable);
            return ResponseEntity.ok().body(campaignResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    public ResponseEntity<?> getListVolunteerNotApproved(int organizationId, int campaignId, int pageNumber, int pageSize){
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            List<ListVolunteerResponse> listVolunteerNotApproved = volunteerJoinCampaignRepository.getListVolunteerNotApproved(organizationId,campaignId,pageable);
            return ResponseEntity.ok().body(listVolunteerNotApproved);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    public ResponseEntity<?> getListVolunteerApproved(int organizationId, int campaignId, int pageNumber, int pageSize){
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            List<ListVolunteerResponse> listVolunteerApproved = volunteerJoinCampaignRepository.getListVolunteerApproved(organizationId,campaignId,pageable);
            return ResponseEntity.ok().body(listVolunteerApproved);
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
            return null;
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    @Transactional
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

            CampaignNode campaignNode = neo4j_CampaignRepository.findCampaignNodeByCampaignId(String.valueOf(campaignId));
            campaignNode.setStatus(CampaignStatus.COMPLETE.toString());
            neo4j_CampaignRepository.save(campaignNode);

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

            CampaignType campaignType = campaignTypeRepository.getCampaignTypeByCampaignId(campaignId);
            if (campaignType == null) {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Can't find campaign type"));
            }
            ObjectMapper objectMapper = new ObjectMapper();
            campaignType = objectMapper.readValue(campaignDto.getCategories(), CampaignType.class);
            campaignTypeRepository.save(campaignType);

            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration()
                    .setMatchingStrategy(MatchingStrategies.STRICT)
                    .setSkipNullEnabled(true);
            modelMapper.map(campaignDto, campaign);
            campaign.setUpdateDate(new Date());
            campaign.setUpdateBy(user.getUserId());
            campaignRepository.save(campaign);

            return ResponseEntity.ok().body(new SuccessReponse("Campaign updated successfully."));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> confirmSettlements(int settlementId, Principal userConnected) {
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) userConnected).getPrincipal();
            Organization organization = organizationRepository.findByUserId(user.getUserId()).get();
            Settlement settlement = settlementRepository.findBySettlementId(settlementId);
            if (organization.getOrganizationId().equals(settlement.getOrganizationId())){
                settlement.setAdminConfirm(true);
                settlement.setUpdateTime(new Date());
                settlementRepository.save(settlement);
                return ResponseEntity.ok().body("Confirm success");
            } else {
                return ResponseEntity.status(HttpStatusCode.valueOf(406)).body("You aren't permit to confirm this settlement");
            }
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.toString());
        }
    }
}
