package com.unidy.backend.services.servicesIplm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.unidy.backend.S3.S3Service;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.Type.NotificationType;
import com.unidy.backend.domains.Type.VolunteerStatus;
import com.unidy.backend.domains.dto.notification.NotificationDto;
import com.unidy.backend.domains.dto.notification.extraData.ExtraData;
import com.unidy.backend.domains.dto.notification.extraData.NewCampaignData;
import com.unidy.backend.domains.dto.requests.CampaignRequest;
import com.unidy.backend.domains.dto.responses.CampaignPostResponse;
import com.unidy.backend.domains.dto.responses.CommentResponse;
import com.unidy.backend.domains.dto.responses.InteractResponse;
import com.unidy.backend.domains.dto.responses.TransactionResponse;
import com.unidy.backend.domains.entity.*;
import com.unidy.backend.domains.entity.neo4j.CampaignNode;
import com.unidy.backend.domains.entity.neo4j.CommentNode;
import com.unidy.backend.domains.entity.neo4j.UserNode;
import com.unidy.backend.domains.entity.relationship.CampaignType;
import com.unidy.backend.domains.role.Role;
import com.unidy.backend.firebase.FirebaseService;
import com.unidy.backend.repositories.*;
import com.unidy.backend.services.servicesInterface.CampaignService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CampaignServiceIplm implements CampaignService {
    private final S3Service s3Service;
    private final Neo4j_CampaignRepository neo4jCampaignRepository;
    private final Neo4j_UserRepository neo4jUserRepository;
    private final VolunteerJoinCampaignRepository volunteerJoinCampaignRepository;
    private final FavoriteActivitiesRepository favoriteActivitiesRepository;
    private final CampaignRepository campaignRepository;
    private final Environment environment;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final FirebaseService firebaseService;
    private final CampaignTypeRepository campaignTypeRepository;
    private final CommentRepository commentRepository;
    private final Neo4j_CommentRepository neo4jCommentRepository;
    private final TransactionRepository transactionRepository;
    private final Neo4j_UserRepository neo4j_UserRepository;

    @Override
    @Transactional
    public ResponseEntity<?> createCampaign(Principal connectedUser, CampaignRequest request) throws JsonProcessingException {
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

            JSONArray listImageLink =  new JSONArray();
            if (null != request.getListImageFile()){
                for (MultipartFile image : request.getListImageFile()){
                    String postImageId = UUID.randomUUID().toString();
                    String fileContentType = image.getContentType();
                    try {
                        if (fileContentType != null &&
                                (fileContentType.equals("image/png") ||
                                        fileContentType.equals("image/jpeg") || fileContentType.equals("image/webp") ||
                                        fileContentType.equals("image/jpg"))) {
                            fileContentType = fileContentType.replace("image/",".");
                            s3Service.putObject(
                                    "unidy",
                                    fileContentType,
                                    "campaign-images/%s/%s".formatted(user.getUserId(), postImageId+fileContentType ),
                                    image.getBytes()
                            );

                            String imageUrl = environment.getProperty("LINK_S3") + "campaign-images/" + user.getUserId() + "/" + postImageId + fileContentType;
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
                    .numberVolunteer(Integer.valueOf(request.getNumberVolunteer()))
                    .donationBudget(Integer.valueOf(request.getDonationBudget()))
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .timeTakePlace(request.getTimeTakePlace())
                    .location(request.getLocation())
                    .createDate(new Timestamp(System.currentTimeMillis()))
                    .hashTag(request.getHashTag())
                    .link_image(listImageLink.toString())
                    .owner(user.getUserId())
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
            campaign.setNumOfRegister(Integer.parseInt(request.getNumberVolunteer()));
            campaign.setCreateDate(dateFormat.format(new Date()));
            campaign.setStartDate(request.getStartDate().toString());
            campaign.setEndDate(request.getEndDate().toString());
            campaign.setTimeTakePlace(request.getTimeTakePlace().toString());
            campaign.setLocation(request.getLocation());
            campaign.setIsBlock(false);
            campaign.setHashTag(request.getHashTag());
            campaign.setUserNode(campaignOrganization);
            campaign.setLinkImage(listImageLink.toString());
            campaign.setUpdateDate(null);
            campaign.setDonationBudget(Integer.parseInt(request.getDonationBudget()));
            campaign.setDonationBudgetReceived(0);
            neo4jCampaignRepository.save(campaign);

            Optional<Organization> organization = organizationRepository.findByUserId(user.getUserId());
            if (organization.isPresent()) {
                ExtraData extraData = new NewCampaignData(campaign_mysql.getCampaignId(), organization.get().getOrganizationId(), request.getTitle());

                NotificationDto notification = NotificationDto.builder()
                        .title(organization.get().getOrganizationName() + " tổ chức chiến dịch mới")
                        .body(request.getDescription())
                        .topic(organization.get().getFirebaseTopic())
                        .extraData(extraData)
                        .build();
                firebaseService.pushNotificationToTopic(notification);
                List<Integer> followerIds = neo4j_UserRepository.getFollowers(user.getUserId());
                Map<String, String> extra = new HashMap<>();
                extra.put("id", campaign.getCampaignId().toString());
                firebaseService.saveNotification(organization.get().getUserId(), followerIds,
                        NotificationType.NEW_CAMPAIGN, organization.get().getOrganizationName() + " tổ chức chiến dịch mới",
                        request.getDescription().substring(0, Math.min(request.getDescription().length(), 100)), new Gson().toJson(extra));
            }

            return ResponseEntity.ok().body(new SuccessReponse("Create campaign success")) ;
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Async("threadPoolTaskExecutor")
    protected CompletableFuture<Integer> saveCampaignToMySQL(CampaignRequest request, User user, String linkImages, Campaign campaign) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            campaign.setTitle(request.getTitle());
            campaign.setDescription(request.getDescription());
            campaign.setStatus(request.getStatus());
            campaign.setNumberVolunteer(Integer.valueOf(request.getNumberVolunteer()));
            campaign.setDonationBudget(Integer.valueOf(request.getDonationBudget()));
            campaign.setStartDate(dateFormat.parse(dateFormat.format(request.getStartDate())));
            campaign.setEndDate(dateFormat.parse(dateFormat.format(request.getEndDate())));
            campaign.setTimeTakePlace(dateFormat.parse(dateFormat.format(request.getTimeTakePlace())));
            campaign.setLocation(request.getLocation());
            campaign.setCreateDate(new Timestamp(System.currentTimeMillis()));
            campaign.setHashTag(request.getHashTag());
            campaign.setLink_image(linkImages);
            campaign.setOwner(user.getUserId());
            campaign.setNumberVolunteerRegistered(0);
            campaignRepository.save(campaign);

            int campaignId = campaign.getCampaignId();
            ObjectMapper objectMapper = new ObjectMapper();
            CampaignType campaignType = objectMapper.readValue(request.getCategories(), CampaignType.class);
            campaignType.setCampaignId(campaignId);
            campaignTypeRepository.save(campaignType);

            return CompletableFuture.completedFuture(0);
        }
        catch (Exception e){
            return CompletableFuture.completedFuture(1);
        }
    }

    @Async("threadPoolTaskExecutor")
    protected CompletableFuture<Integer> saveCampaignToNeo4J(CampaignRequest request, User user, String linkImages, Campaign campaign) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            UserNode campaignOrganization = neo4jUserRepository.findUserNodeByUserId(user.getUserId());
            CampaignNode campaignNode = new CampaignNode() ;
            campaignNode.setCampaignId(campaign.getCampaignId().toString());
            campaignNode.setContent(request.getDescription());
            campaignNode.setTitle(request.getTitle());
            campaignNode.setStatus(request.getStatus());
            campaignNode.setNumOfRegister(Integer.parseInt(request.getNumberVolunteer()));
            campaignNode.setCreateDate(sdf.format(new Date()));
            campaignNode.setStartDate(sdf.format(request.getStartDate()));
            campaignNode.setEndDate(sdf.format(request.getEndDate()));
            campaignNode.setTimeTakePlace(sdf.format(request.getTimeTakePlace()));
            campaignNode.setLocation(request.getLocation());
            campaignNode.setIsBlock(false);
            campaignNode.setHashTag(request.getHashTag());
            campaignNode.setUserNode(campaignOrganization);
            campaignNode.setLinkImage(linkImages);
            campaignNode.setUpdateDate(null);
            campaignNode.setDonationBudget(Integer.parseInt(request.getDonationBudget()));
            campaignNode.setDonationBudgetReceived(0);
            neo4jCampaignRepository.save(campaignNode);
            return CompletableFuture.completedFuture(0);
        }
        catch (Exception e){
            return CompletableFuture.completedFuture(1);
        }
    }

    @Transactional
    public ResponseEntity<?> registerCampaign(Principal userConnected, int campaignId){
        try{
            var user = (User) ((UsernamePasswordAuthenticationToken) userConnected).getPrincipal();
            VolunteerJoinCampaign data = volunteerJoinCampaignRepository.findVolunteerJoinCampaignByUserIdAndCampaignId(user.getUserId(),campaignId);
            if (data != null){
                return ResponseEntity.badRequest().body(new ErrorResponseDto("you have joined yet"));
            }

            VolunteerJoinCampaign userJoin = new VolunteerJoinCampaign();
            userJoin.setUserId(user.getUserId());
            userJoin.setCampaignId(campaignId);
            userJoin.setTimeJoin(new Date());
            userJoin.setStatus(String.valueOf(VolunteerStatus.NOT_APPROVE_YET));

            Campaign campaign = campaignRepository.findCampaignByCampaignId(campaignId);
            if (campaign == null){
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Không tìm thấy chiến dịch"));
            }
            if (campaign.getNumberVolunteerRegistered() >= campaign.getNumberVolunteer()){
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Chiến dịch đã đủ số lượng tình nguyện viên"));
            } else {
                campaign.setNumberVolunteerRegistered(campaign.getNumberVolunteerRegistered()+1);
                campaignRepository.save(campaign);
            }

            volunteerJoinCampaignRepository.save(userJoin);
            return  ResponseEntity.ok().body(new SuccessReponse("Tham gia thành công"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Tham gia thất bại"));
        }

    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<CampaignPostResponse.CampaignPostResponseData>> getRecommendationFromKNearest(Principal connectedUser, int offset, int limit) throws Exception {
        String api_recommendation_flask = environment.getProperty("API_RECOMMENDATION_FLASK");

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        FavoriteActivities favoriteActivities = favoriteActivitiesRepository.findByUserId(user.getUserId());

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Double> attribute = Arrays.asList(favoriteActivities.getCommunityType(), favoriteActivities.getEducation(),
                favoriteActivities.getResearch(), favoriteActivities.getHelpOther(), favoriteActivities.getEnvironment(), favoriteActivities.getHealthy(), favoriteActivities.getEmergencyPreparedness());
        List<List<Double>> requestData = List.of(attribute);

        HttpEntity<List<List<Double>>> requestEntity = new HttpEntity<>(requestData, headers);
        assert api_recommendation_flask != null;
        ResponseEntity<String> response = restTemplate.postForEntity(api_recommendation_flask, requestEntity, String.class);
        String responseData = response.getBody();
        if (response.getStatusCode() == HttpStatusCode.valueOf(500)) {
            throw new Exception("Can't call api from recommend service");
        }
        String[] campaignIds = new Gson().fromJson(responseData, String[].class);
        int length = Math.min(offset + limit, campaignIds.length);
        String[] splitIds = Arrays.copyOfRange(campaignIds, offset, length);
        List<CampaignPostResponse.CampaignPostResponseData> campaignPostResponseData = neo4jCampaignRepository.findCampaignPostByCampaignIds(user.getUserId(), splitIds);
        List<CampaignPostResponse.CampaignPostResponseData> result = userJoinedCampaignMapping(campaignPostResponseData, user.getUserId());
        return CompletableFuture.supplyAsync(() -> result);
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<CampaignPostResponse.CampaignPostResponseData>> getRecommendationFromNeo4J(Principal connectedUser, int skip, int limit) throws Exception {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        List<CampaignPostResponse.CampaignPostResponseData> listCampaign = neo4jCampaignRepository.findCampaign(user.getUserId(), skip, limit);
        List<CampaignPostResponse.CampaignPostResponseData> result = userJoinedCampaignMapping(listCampaign, user.getUserId());
        return CompletableFuture.supplyAsync(() -> result);
    }

    public List<CampaignPostResponse.CampaignPostResponseData> getCampaignByOrganizationID(Principal connectedUser, int organizationId, int skip, int limit){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        User organization = userRepository.findByUserId(organizationId);
        if (organization == null || !organization.getRole().equals(Role.ORGANIZATION)){
            throw new RuntimeException("id tổ chức không hợp lệ");
        }

        List<CampaignPostResponse.CampaignPostResponseData> listCampaign = neo4jCampaignRepository.findCampaignByOrganizationID(user.getUserId(), organizationId, skip, limit);
        listCampaign = userJoinedCampaignMapping(listCampaign, user.getUserId());
        return listCampaign;
    }

    private List<CampaignPostResponse.CampaignPostResponseData> userJoinedCampaignMapping(List<CampaignPostResponse.CampaignPostResponseData> campaigns, int userId) {
        List<Integer> campaignIds = new ArrayList<>();
        campaigns = deDuplicate(campaigns);
        for (CampaignPostResponse.CampaignPostResponseData campaign : campaigns) {
            campaignIds.add(Integer.parseInt(campaign.getCampaign().getCampaignId()));
        }
        List<VolunteerJoinCampaign> userJoinedCampaigns = volunteerJoinCampaignRepository.findVolunteerJoinCampaignByCampaignIdInAndUserId(campaignIds, userId);
        Map<Integer, VolunteerStatus> userJoinedCampaignMap = new HashMap<>();
        for (VolunteerJoinCampaign userJoinedCampaign : userJoinedCampaigns) {
            userJoinedCampaignMap.put(userJoinedCampaign.getCampaignId(), VolunteerStatus.valueOf(userJoinedCampaign.getStatus()));
        }

        for (CampaignPostResponse.CampaignPostResponseData campaign : campaigns) {
            if (userJoinedCampaignMap.containsKey(Integer.parseInt(campaign.getCampaign().getCampaignId()))) {
                campaign.setIsJoined(true);
                campaign.setJoinedStatus(userJoinedCampaignMap.get(Integer.parseInt(campaign.getCampaign().getCampaignId())));
            }
        }

        return campaigns;
    }

    private List<CampaignPostResponse.CampaignPostResponseData> deDuplicate(List<CampaignPostResponse.CampaignPostResponseData> campaigns) {
        List<CampaignPostResponse.CampaignPostResponseData> result = new ArrayList<>();
        Set<String> campaignIds = new HashSet<>();
        for (CampaignPostResponse.CampaignPostResponseData campaign : campaigns) {
            if (!campaignIds.contains(campaign.getCampaign().getCampaignId())) {
                result.add(campaign);
                campaignIds.add(campaign.getCampaign().getCampaignId());
            }
        }
        return result;
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<CampaignPostResponse.CampaignPostResponseData>> searchCampaign(Principal connectedUser, String searchTerm, int limit, int skip) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        List<CampaignPostResponse.CampaignPostResponseData> campaigns = neo4jCampaignRepository.searchCampaign(user.getUserId(), searchTerm, limit, skip);
        return CompletableFuture.supplyAsync(() -> campaigns);
    }

    @Override
    public ResponseEntity<?> getTransactionByCampaignId(int campaignId, int pageNumber, int pageSize) {
        try {
            Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
            List<TransactionResponse> transactions = transactionRepository.findTransactionsByCampaignIdAndGroupByUserId(campaignId, pageable);
            return ResponseEntity.ok().body(transactions);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Không thể lấy danh sách giao dịch"));
        }
    }

    @Override
    public ResponseEntity<?> likeCampaign(Principal connectedUser, String campaignId) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            CampaignNode campaign = neo4jCampaignRepository.findCampaignNodeByCampaignId(campaignId);
            if (campaign == null){
                return ResponseEntity.notFound().build();
            }
            UserNode userNode = neo4jUserRepository.findUserNodeByUserId(user.getUserId());
            if (userNode == null) {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Có lỗi xảy ra khi like bài viết"));
            }
            boolean isAlreadyLiked = neo4jCampaignRepository.isLikedCampaign(user.getUserId(), campaignId);
            if (isAlreadyLiked) {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Bạn đã like bài viết này rồi"));
            }

            List<UserNode> userLikes = campaign.getUserLikes();
            userLikes.add(userNode);
            campaign.setUserLikes(userLikes);
            neo4jCampaignRepository.save(campaign);
            return ResponseEntity.ok().body(new InteractResponse("Like chiến dịch thành công", userLikes.size()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something Error"));
        }
    }

    @Override
    public ResponseEntity<?> cancelLikeCampaign(Principal connectedUser, String campaignId) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        try {
            UserNode userNode = neo4jUserRepository.findUserNodeByUserId(user.getUserId());

            try {
                CampaignNode campaign =  neo4jCampaignRepository.findCampaignNodeByCampaignId(campaignId);
                if (campaign != null) {
                    boolean isAlreadyLiked = neo4jCampaignRepository.isLikedCampaign(user.getUserId(), campaignId);
                    if (!isAlreadyLiked) {
                        return ResponseEntity.badRequest().body(new ErrorResponseDto("Bạn chưa like bài viết này"));
                    }

                    neo4jCampaignRepository.cancelLikeCampaign(user.getUserId(),campaignId);
                    return ResponseEntity.ok().body(new InteractResponse("Hủy like chiến dịch thành công", campaign.getUserLikes().size() - 1));
                }
                else {
                    return ResponseEntity.notFound().build();
                }
            }
            catch(Exception e){
                return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
            }
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something Error"));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> comment(Principal connectedUser, String campaignId, String content) {
        try {
            if (campaignId == null) {
                return ResponseEntity.badRequest().body("Campaign id must not be null");
            }

            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            Comment mysqlComment = Comment.builder()
                    .content(content)
                    .createTime(new Date())
                    .idBlock(false)
                    .replyByComment(null)
                    .build();
            commentRepository.save(mysqlComment);

            CampaignNode campaign = neo4jCampaignRepository.findCampaignNodeByCampaignId(campaignId);
            UserNode userComment = neo4jUserRepository.findUserNodeByUserId(user.getUserId());
            CommentNode comment = CommentNode.builder()
                    .commentId(mysqlComment.getCommentId())
                    .body(content)
                    .build();
            comment.setUserComment(userComment);
            comment.setCampaignNode(campaign);
            neo4jCommentRepository.save(comment);
            return ResponseEntity.ok().body("Comment success");
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Comment fail");
        }
    }

    @Override
    public ResponseEntity<?> getComment(Principal connectedUser, String campaignId, int skip, int limit) {
        try {
            List<CommentResponse> listComment = neo4jCommentRepository.getAllCommentByCampaignId(campaignId,skip,limit);
            return ResponseEntity.ok().body(listComment);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> replyComment(Principal connectedUser, Integer commentId, String content) {
        try {
            if (commentId == null) {
                return ResponseEntity.badRequest().body("Comment id must not be null");
            }

            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            Comment mysqlComment = Comment.builder()
                    .content(content)
                    .createTime(new Date())
                    .idBlock(false)
                    .replyByComment(null)
                    .build();
            commentRepository.save(mysqlComment);

            CommentNode comment = neo4jCommentRepository.findCommentNodeByCommentId(commentId);
            UserNode userComment = neo4jUserRepository.findUserNodeByUserId(user.getUserId());
            CommentNode reply = CommentNode.builder()
                    .commentId(mysqlComment.getCommentId())
                    .body(content)
                    .build();
            reply.setUserComment(userComment);
            comment.setReplyComment(reply);
            neo4jCommentRepository.save(reply);
            neo4jCommentRepository.save(comment);
            return ResponseEntity.ok().body("Comment success");
        } catch (Exception e){
            return ResponseEntity.ok().body("Comment fail");
        }
    }

    @Override
    public ResponseEntity<?> getReplyComment(Principal connectedUser, Integer commentId, int skip, int limit) {
        try {
            List<CommentResponse> listReplyComment = neo4jCommentRepository.getAllReplyComment(commentId, skip, limit);
            return ResponseEntity.ok().body(listReplyComment);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @Override
    public ResponseEntity<?> getCampaignByCampaignId(Principal connectedUser, String campaignId) {
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            List<CampaignPostResponse.CampaignPostResponseData> campaigns = neo4jCampaignRepository.findCampaignNodeByCampaignIdCustom(user.getUserId(), campaignId);
            if (campaigns == null){
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Can't find this post"));
            }
            campaigns = deDuplicate(campaigns);
            VolunteerStatus volunteerStatus = volunteerJoinCampaignRepository.findVolunteerStatusByUserIdAndCampaignId(user.getUserId(), campaignId);
            campaigns.get(0).setJoinedStatus(volunteerStatus);
            return ResponseEntity.ok().body(campaigns.get(0));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.toString());
        }
    }
}
