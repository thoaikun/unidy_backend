package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.config.JwtService;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.TokenType;
import com.unidy.backend.domains.Type.CampaignStatus;
import com.unidy.backend.domains.dto.requests.AuthenticationRequest;
import com.unidy.backend.domains.dto.requests.PostCondition;
import com.unidy.backend.domains.dto.requests.RegisterRequest;
import com.unidy.backend.domains.dto.responses.*;
import com.unidy.backend.domains.entity.*;
import com.unidy.backend.domains.entity.neo4j.PostNode;
import com.unidy.backend.domains.role.Role;
import com.unidy.backend.repositories.*;
import com.unidy.backend.services.servicesInterface.AdminService;
import lombok.RequiredArgsConstructor;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final ScheduleJobsRepository scheduleJobsRepository;
    private final Scheduler scheduler;
    private final Neo4j_PostRepository neo4jPostRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AdminRepository adminRepository;
    private final AdminTokenRepository adminTokenRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;
    private final SettlementRepository settlementRepository;
    private final TransactionRepository transactionRepository;
    private final VolunteerJoinCampaignRepository volunteerJoinCampaignRepository;
    private final PostRepository postRepository;

    public ResponseEntity<?> register(RegisterRequest request) {
        try {
            var findUser = adminRepository.findByEmail(request.getEmail());
            if (findUser.isPresent()) {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Invalid Email"));
            }


            var user = Admin.builder()
                    .fullName(request.getFullName())
                    .dayOfBirth(request.getDayOfBirth())
                    .phone(request.getPhone())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .build();
            var savedUser = adminRepository.save(user);
            var jwtToken = jwtService.generateToken(user);
            saveUserToken(savedUser, jwtToken, jwtToken);


            return ResponseEntity.ok().header("Register").body("Register success");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e);
        }
    }

    @Override
    public ResponseEntity<?> approveOrganization(int organizationId) {
        try {
            Organization organization = organizationRepository.findByOrganizationId(organizationId);
            organization.setIsApproved(true);
            organizationRepository.save(organization);
            return ResponseEntity.ok().body("Approve Success");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @Override
    public ResponseEntity<?> getAllVolunteers(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<UserInfoForAdminResponse> users = userRepository.getAllUserInfoForAdminByRole(Role.VOLUNTEER, pageable);
            return ResponseEntity.ok().body(users);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Có lỗi xảy ra"));
        }
    }

    @Override
    public ResponseEntity<?> getAllOrganizations(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<OrganizationInformation> organizations = organizationRepository.getOrganizations(pageable);
            return ResponseEntity.ok().body(organizations);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Có lỗi xảy ra"));
        }
    }

    @Override
    public ResponseEntity<?> blockOrUnblockUser(int userId) {
        try {
            User user = userRepository.findByUserId(userId);
            user.setIsBlock(!user.getIsBlock());
            userRepository.save(user);
            return ResponseEntity.ok().body("Success");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @Override
    public ResponseEntity<?> confirmSettlements(int settlementId, Principal userConnected) {
        try {
            Settlement settlement = settlementRepository.findBySettlementId(settlementId);
            settlement.setAdminConfirm(true);
            settlement.setUpdateTime(new Date());
            settlementRepository.save(settlement);
            return ResponseEntity.ok().body("Confirm success");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @Override
    public ResponseEntity<?> getCampaign(PostCondition postCondition) {
        try {
            Date fromDate = postCondition.getFromDate() != null ? postCondition.getFromDate() : Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant());
            Date toDate = postCondition.getToDate() != null ? postCondition.getToDate() : Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
            int pageNumber = postCondition.getPageNumber() != null ? postCondition.getPageNumber() : 0;
            int pageSize = postCondition.getPageSize() != null ? postCondition.getPageSize() : 10;
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Campaign> campaigns;
            if (postCondition.getStatus() != null) {
                campaigns = campaignRepository.getCampaignsByStatusAndCreateDateBetween(postCondition.getStatus().toString(), fromDate, toDate, pageable);
            } else {
                campaigns = campaignRepository.getCampaignsByCreateDateBetween(fromDate, toDate, pageable);
            }
            return ResponseEntity.ok().body(campaigns);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @Override
    public ResponseEntity<?> getCampaignByCampaignId(Integer campaignId) {
        try {
            Campaign campaign = campaignRepository.findCampaignByCampaignId(campaignId);
            return ResponseEntity.ok().body(campaign);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> getTransactionByCampaignId(Integer campaignId, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<TransactionResponse> transactions = transactionRepository.findTransactionsByCampaignId(campaignId, pageable);
            return ResponseEntity.ok().body(transactions);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> getApprovedVolunteers(int organizationId, int campaignId, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<ListVolunteerResponse> volunteers = volunteerJoinCampaignRepository.getListVolunteerApproved(organizationId, campaignId, pageable);
            return ResponseEntity.ok().body(volunteers);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> getNotApprovedVolunteers(int organizationId, int campaignId, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<ListVolunteerResponse> volunteers = volunteerJoinCampaignRepository.getListVolunteerNotApproved(organizationId, campaignId, pageable);
            return ResponseEntity.ok().body(volunteers);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> getNotApprovedOrganizations(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<OrganizationInformation> organizations = organizationRepository.getOrganizationByIsApproved(false, pageable);
            return ResponseEntity.ok().body(organizations);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> getVolunteerByVolunteerId(int volunteerId) {
        try {
            UserInfoForAdminResponse user = userRepository.getUserInfoForAdminById(volunteerId);
            return ResponseEntity.ok().body(user);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> getOrganizationInformation(int organizationId) {
        try {
            OrganizationInformation organizationInformation = organizationRepository.getOrganizationInformation(organizationId);
            if (organizationInformation == null)
                return ResponseEntity.notFound().build();

            return ResponseEntity.ok().body(organizationInformation);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> getPostByPostId(String postId) {
        try {
            Post post = postRepository.getPostByPostId(postId);
            return ResponseEntity.ok().body(post);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var user = adminRepository.findByEmail(request.getEmail())
                    .orElseThrow();

            Map<String, String> jwtTokens = getJwtToken(user);
            if (jwtTokens.get("isExpired").equals("true")){
                saveUserToken(user, jwtTokens.get("accessToken"), jwtTokens.get("refreshToken"));
            }

            return ResponseEntity.ok().body(AuthenticationResponse.builder()
                    .accessToken(jwtTokens.get("accessToken"))
                    .refreshToken(jwtTokens.get("refreshToken"))
                    .role(user.getRole())
                    .build());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto("Email hoặc mật khẩu không đúng"));
        }
    }

    @Override
    public ResponseEntity<?> runOrStopJob(int jobId) {
        Optional<ScheduleJobs> optionalScheduleJobs = scheduleJobsRepository.findById(jobId);

        if (optionalScheduleJobs.isPresent()) {
            ScheduleJobs scheduleJob = optionalScheduleJobs.get();
            try {
                JobKey jobKey = new JobKey("scheduleJob" + scheduleJob.getId(), "group1");
                if (scheduleJob.getStatus().equals("STOP")) {
                    scheduleJob.setStatus("RUNNING");
                    scheduleJobsRepository.save(scheduleJob);
                    scheduler.resumeJob(jobKey);
                    return ResponseEntity.ok().body(new SuccessReponse("Running job: "+ jobId));
                } else {
                    scheduleJob.setStatus("STOP");
                    scheduleJobsRepository.save(scheduleJob);
                    scheduler.pauseJob(jobKey);
                    return ResponseEntity.ok().body(new SuccessReponse("Stop job: "+ jobId));
                }
            } catch (SchedulerException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to start/pause job.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<?> blockOrUnblockPost(String postId, String status) {
        try {
            List<PostNode> postNode =  neo4jPostRepository.findPostNodeByPostId(String.valueOf(postId));
            PostNode post = postNode.get(0);
            post.setIsBlock(Objects.equals(status, "1"));
            neo4jPostRepository.save(post);
            return ResponseEntity.ok().body(new SuccessReponse("Success"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> deletePost(String postId) {
        try {
            neo4jPostRepository.deletePostByPostId(postId);
            return ResponseEntity.ok().body(new SuccessReponse("Delete success"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> getPostByDate(Date fromDate, Date toDate, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Post> posts = postRepository.getPostsByCreateDateBetween(fromDate, toDate, pageable);
            return ResponseEntity.ok().body(posts);
        } catch (Exception e){
            return  ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    private Map<String, String> getJwtToken(Admin user) {
        List<AdminToken> previousTokens = adminTokenRepository.findAllValidTokenByUser(user.getAdmin_id());

        for (AdminToken token : previousTokens) {
            try {
                isTokenStillValid(token);
                return Map.of("accessToken", token.getToken(), "refreshToken", token.getRefreshToken(), "isExpired", "false");
            }
            catch (Exception e){
                token.setExpired(true);
                token.setRevoked(true);
                adminTokenRepository.save(token);
            }
        }

        return Map.of("accessToken", jwtService.generateToken(user), "refreshToken", jwtService.generateRefreshToken(user), "isExpired", "true");
    }

    private void saveUserToken(Admin user, String jwtToken, String refreshToken) {
        var token = AdminToken.builder()
                .admin(user)
                .token(jwtToken)
                .refreshToken(refreshToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        adminTokenRepository.save(token);
    }

    private void isTokenStillValid(AdminToken token) {
        jwtService.isTokenExpired(token.getToken());
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = adminTokenRepository.findAllValidTokenByUser(user.getUserId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        adminTokenRepository.saveAll(validUserTokens);
    }
}
