package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.S3.S3Service;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.dto.requests.CampaignRequest;
import com.unidy.backend.domains.entity.CampaignNode;
import com.unidy.backend.domains.entity.User;
import com.unidy.backend.domains.entity.UserNode;
import com.unidy.backend.domains.entity.VolunteerJoinCampaign;
import com.unidy.backend.pubnub.PubnubService;
import com.unidy.backend.repositories.Neo4j_CampaignRepository;
import com.unidy.backend.repositories.Neo4j_UserRepository;
import com.unidy.backend.repositories.VolunteerJoinCampaignRepository;
import com.unidy.backend.services.servicesInterface.CampaignService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CampaignServiceIplm implements CampaignService {
    private final S3Service s3Service;
    private final Neo4j_CampaignRepository campaignRepository;
    private final Neo4j_UserRepository neo4jUserRepository;
    private final VolunteerJoinCampaignRepository joinCampaign;
    private final PubnubService pubnubService;
    @Override
    public ResponseEntity<?> createCampaign(Principal connectedUser, CampaignRequest request) {
        //MySQL


        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        //neo4j
        UserNode campaignOrganization = neo4jUserRepository.findUserNodeByUserId(user.getUserId());
        CampaignNode campaign = new CampaignNode() ;
        campaign.setCampaignId(LocalDateTime.now().toString()+'_'+user.getUserId().toString());
        campaign.setContent(request.getContent());
        campaign.setStatus(request.getStatus());
        campaign.setNumOfRegister(request.getNumOfVolunteer());
        campaign.setCreateDate(new Date().toString());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());
        campaign.setIsBlock(false);
        campaign.setHashTag(request.getHashTag());
        campaign.setUserNode(campaignOrganization);
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
                                "post-images/%s/%s".formatted(user.getUserId(), postImageId+fileContentType ),
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
        campaign.setLinkImage(listImageLink.toString());
        campaign.setUpdateDate(null);


        campaignRepository.save(campaign);

        return ResponseEntity.ok().body(new SuccessReponse("Create campaign success")) ;
    }
    public ResponseEntity<?> registerCampaign(Principal userConnected, int campaignId){
        var user = (User) ((UsernamePasswordAuthenticationToken) userConnected).getPrincipal();
        VolunteerJoinCampaign userJoin = new VolunteerJoinCampaign();
        userJoin.setVolunteerId(user.getUserId());
        userJoin.setCampaignId(campaignId);
        userJoin.setTimeJoin(new Date());
        userJoin.setStatus("join");
//        joinCampaign.save(userJoin);
        pubnubService.sendNotification("a","Join campaign successful");
        return  ResponseEntity.ok().body(new SuccessReponse("Join success"));
    }

}
