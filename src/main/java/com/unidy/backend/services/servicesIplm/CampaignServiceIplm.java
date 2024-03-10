package com.unidy.backend.services.servicesIplm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidy.backend.S3.S3Service;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.Type.VolunteerStatus;
import com.unidy.backend.domains.dto.requests.CampaignRequest;
import com.unidy.backend.domains.dto.responses.CampaignPostResponse;
import com.unidy.backend.domains.entity.*;
import com.unidy.backend.domains.entity.relationship.CampaignType;
import com.unidy.backend.repositories.*;
import com.unidy.backend.services.servicesInterface.CampaignService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.core.env.Environment;
import software.amazon.awssdk.services.s3.model.Owner;

@Service
@RequiredArgsConstructor
public class CampaignServiceIplm implements CampaignService {
    private final S3Service s3Service;
    private final Neo4j_CampaignRepository neo4jCampaignRepository;
    private final Neo4j_UserRepository neo4jUserRepository;
    private final VolunteerJoinCampaignRepository joinCampaign;
    private final FavoriteActivitiesRepository favoriteActivitiesRepository;
    private final CampaignRepository campaignRepository;
    private final Environment environment;
    private final OrganizationRepository organizationRepository;
    private final UserProfileImageRepository userProfileImageRepository;
    private final CampaignTypeRepository campaignTypeRepository;
    @Override
    @Transactional
    public ResponseEntity<?> createCampaign(Principal connectedUser, CampaignRequest request) throws JsonProcessingException {
        try {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            //neo4j

        JSONArray listImageLink =  new JSONArray();
        if (null != request.getListImageFile()){
            for (MultipartFile image : request.getListImageFile()){
                String postImageId = UUID.randomUUID().toString();
                String fileContentType = image.getContentType();
                try {
                    if (fileContentType != null &&
                            (fileContentType.equals("image/png") ||
                                    fileContentType.equals("image/jpeg") ||
                                    fileContentType.equals("image/jpg"))) {
                        fileContentType = fileContentType.replace("image/",".");
                        s3Service.putImage(
                                "unidy",
                                fileContentType,
                                "campaign-images/%s/%s".formatted(user.getUserId(), postImageId+fileContentType ),
                                image.getBytes()
                        );

                        String imageUrl = "/" + user.getUserId() + "/" + postImageId + fileContentType;
                        listImageLink.put(imageUrl);
                    } else {
                        return ResponseEntity.badRequest().body(new ErrorResponseDto("Unsupported file format"));
                    }
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
                }
            }
        }


            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Campaign campaign_mysql = Campaign.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .status(request.getStatus())
                    .numberVolunteer(request.getNumOfVolunteer())
                    .donationBudget(request.getDonationBudget())
                    .startDate(request.getStartDate())
                    .endDate((request.getEndDate()))
                    .timeTakePlace(request.getTimeTakePlace())
                    .location(request.getLocation())
                    .createDate(dateFormat.format(new Date()))
                    .hashTag(request.getHashTag())
                    .link_image(listImageLink.toString())
                    .owner(user.getUserId())
                    .categories("OK")
                    .numberVolunteerRegistered(0)
                    .build();
            campaignRepository.save(campaign_mysql);

            int campaignId = campaign_mysql.getCampaignId();
            ObjectMapper objectMapper = new ObjectMapper();
            CampaignType campaignType = objectMapper.readValue(request.getCategories(), CampaignType.class);
            campaignType.setCampaignId(campaignId);
            campaignTypeRepository.save(campaignType);

            UserNode campaignOrganization = neo4jUserRepository.findUserNodeByUserId(user.getUserId());
            CampaignNode campaign = new CampaignNode() ;
            campaign.setCampaignId(campaign_mysql.getCampaignId().toString());
            campaign.setContent(request.getDescription());
            campaign.setTitle(request.getTitle());
            campaign.setStatus(request.getStatus());
            campaign.setNumOfRegister(request.getNumOfVolunteer());
            campaign.setCreateDate(sdf.format(new Date()));
            campaign.setStartDate(request.getStartDate().toString());
            campaign.setEndDate(request.getEndDate().toString());
            campaign.setTimeTakePlace(request.getTimeTakePlace().toString());
            campaign.setLocation(request.getLocation());
            campaign.setIsBlock(false);
            campaign.setHashTag(request.getHashTag());
            campaign.setUserNode(campaignOrganization);
            campaign.setLinkImage(listImageLink.toString());
            campaign.setUpdateDate(null);
            campaign.setDonationBudget(request.getDonationBudget());
            campaign.setDonationBudgetReceived(0);
            neo4jCampaignRepository.save(campaign);


            return ResponseEntity.ok().body(new SuccessReponse("Create campaign success")) ;
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }

    }
    @Transactional
    public ResponseEntity<?> registerCampaign(Principal userConnected, int campaignId){
        try{
            var user = (User) ((UsernamePasswordAuthenticationToken) userConnected).getPrincipal();
            VolunteerJoinCampaign volunteer = joinCampaign.findVolunteerJoinCampaignByVolunteerIdAndCampaignId(user.getUserId(),campaignId);
            if (volunteer != null){
                return ResponseEntity.badRequest().body(new ErrorResponseDto("you have joined yet"));
            }
            VolunteerJoinCampaign userJoin = new VolunteerJoinCampaign();
            userJoin.setVolunteerId(user.getUserId());
            userJoin.setCampaignId(campaignId);
            userJoin.setTimeJoin(new Date());
            userJoin.setStatus(String.valueOf(VolunteerStatus.NOT_APPROVE_YET));

            Campaign campaign = campaignRepository.findCampaignByCampaignId(campaignId);
            if (campaign.getNumberVolunteerRegistered() >= campaign.getNumberVolunteer()){
                return ResponseEntity.ok().body(new ErrorResponseDto("Full slot"));
            } else {
                campaign.setNumberVolunteerRegistered(campaign.getNumberVolunteerRegistered()+1);
                campaignRepository.save(campaign);
            }

            joinCampaign.save(userJoin);
            return  ResponseEntity.ok().body(new SuccessReponse("Join success"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }

    }


    @Transactional
    public ResponseEntity<?> getRecommend(Principal connectedUser, int offset, int limit) {
        try {

            String api_recommendation_flask = environment.getProperty("API_RECOMMENDATION_FLASK");


            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            FavoriteActivities favoriteActivities = favoriteActivitiesRepository.findByUserId(user.getUserId());

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            List<Double> attribute = Arrays.asList(favoriteActivities.getCommunityType(),favoriteActivities.getEducation(),
                    favoriteActivities.getResearch(),favoriteActivities.getHelpOther(),favoriteActivities.getEnvironment(),favoriteActivities.getHealthy(),favoriteActivities.getEmergencyPreparedness());
            List<List<Double>> requestData = List.of(attribute);

            HttpEntity<List<List<Double>>> requestEntity = new HttpEntity<>(requestData, headers);
//            String flaskAPIRecommend = "http://0.0.0.0:8000/recommend-campaign";
            assert api_recommendation_flask != null;
            ResponseEntity<String> response = restTemplate.postForEntity(api_recommendation_flask, requestEntity, String.class);
            String responseData = response.getBody();
            if (response.getStatusCode() == HttpStatusCode.valueOf(500)){
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Can't call api from recommend service"));
            }
            System.out.println(responseData);
            List<CampaignPostResponse> responses = new ArrayList<>();
            int[] arrayId = Arrays.copyOfRange(stringToArray(responseData),offset,offset+limit);
            for (int id : arrayId) {
//                Optional<Campaign> campaign = campaignRepository.findById(id);
//                Campaign info = campaign.get();
//                Optional<Organization> organization = organizationRepository.findByUserId(info.getOwner());
//                UserProfileImage userProfileImage = userProfileImageRepository.findByUserId(info.getOwner());
//                CampaignResponse campaignInfo = CampaignResponse.builder()
//                        .campaignId(info.getCampaignId())
//                        .title(info.getTitle())
//                        .description(info.getDescription())
//                        .categories(info.getCategories())
//                        .numberVolunteer(info.getNumberVolunteer())
//                        .numberVolunteerRegistered(info.getNumberVolunteerRegistered())
//                        .donationBudget(info.getDonationBudget())
//                        .donationBudgetReceived(info.getDonationBudgetReceived())
//                        .startDate(info.getStartDate())
//                        .endDate(info.getEndDate())
//                        .timeTakePlace(info.getTimeTakePlace())
//                        .location(info.getLocation())
//                        .status(info.getStatus())
//                        .createDate(info.getCreateDate())
//                        .updateDate(info.getUpdateDate())
//                        .ownerId(organization.get().getOrganizationId())
//                        .ownerName(organization.get().getOrganizationName())
//                        .ownerProfileImage(null)
//                        .hashTag(info.getHashTag())
//                        .linkImage(info.getLink_image())
//                        .build();
//                if (userProfileImage != null){
//                    campaignInfo.setOwnerProfileImage(userProfileImage.getLinkImage());
//                }

                CampaignPostResponse campaignPost = neo4jCampaignRepository.findCampaignPostByCampaignId(String.valueOf(id));
                responses.add(campaignPost);
            }

            return ResponseEntity.ok().body(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    private int[] stringToArray(String arrayString) {
        arrayString = arrayString.replace("[", "").replace("]", "");
        String[] elements = arrayString.split(", ");
        int[] resultArray = new int[elements.length];
        for (int i = 0; i < elements.length; i++) {
            resultArray[i] = Integer.parseInt(elements[i]);
        }
        return resultArray;
    }

    public ResponseEntity<?> getCampaignPost(Principal connectedUser, String cursor, int limit){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            List<CampaignPostResponse> listCampaign = neo4jCampaignRepository.findCampaign(user.getUserId(),cursor,limit);
            return ResponseEntity.ok().body(listCampaign);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something Error"));
        }
    }

    public ResponseEntity<?> getCampaignByOrganizationID(int organizationId,String cursor,int limit){
        try {
            List<CampaignPostResponse> listCampaign = neo4jCampaignRepository.findCampaignByOrganizationID(organizationId,cursor,limit);
            return ResponseEntity.ok().body(listCampaign);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something Error"));
        }
    }
}
