package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.S3.S3Service;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.dto.responses.CertificateResponse;
import com.unidy.backend.domains.entity.*;
import com.unidy.backend.repositories.*;
import com.unidy.backend.services.servicesInterface.CertificateService;
import com.unidy.backend.utils.export.HtmlTool;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {
    private final S3Service s3Service;
    private final CertificateRepository certificateRepository;
    private final VolunteerCertificateRepository volunteerCertificateRepository;
    private final OrganizationRepository organizationRepository;
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final VolunteerJoinCampaignRepository volunteerJoinCampaignRepository;
    private final Environment environment;

    @Override
    @Transactional
    public ResponseEntity<?> createCertificate(Principal connectedUser, int campaignId) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Optional<Organization> organization = organizationRepository.findByUserId(user.getUserId());
        List<VolunteerJoinCampaign> volunteerJoinCampaigns = volunteerJoinCampaignRepository.findUserIdsByCampaignIdAndStatus(campaignId, "APPROVE");
        Campaign campaign = campaignRepository.findCampaignByCampaignId(campaignId);

        if (organization.isEmpty()){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Can't find organization"));
        }
        try {
            for (VolunteerJoinCampaign volunteerJoinCampaign : volunteerJoinCampaigns) {
                User volunteer = userRepository.findByUserId(volunteerJoinCampaign.getUserId());
                String certificatePath = "certificate/%s.pdf".formatted(volunteer.getUserId()+"_"+ campaignId+"_"+ organization.get().getOrganizationId());
                String certificateLink = environment.getProperty("LINK_S3") + certificatePath;
                generateCertificate(
                    volunteer.getFullName(),
                    organization.get().getOrganizationName(),
                    campaign.getTitle(),
                    campaign.getTimeTakePlace(),
                    certificatePath
                );

                Certificate isExist = certificateRepository.findCertificateByFile(certificateLink);
                if (isExist == null){
                    Certificate certificate = Certificate.builder()
                            .file(certificateLink)
                            .build();
                    certificateRepository.save(certificate);
                    VolunteerCertificate volunteerCertificate = VolunteerCertificate.builder()
                            .volunteerId(volunteer.getUserId())
                            .campaignId(campaignId)
                            .certificateId(certificate.getCertificateId())
                            .build();
                    volunteerCertificateRepository.save(volunteerCertificate);
                }
            }

            return ResponseEntity.ok().body(new SuccessReponse("Certificate created successfully"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something went wrong while creating the certificate"));
        }
    }

    @Override
    public ResponseEntity<?> getCertificate(Principal connectedUser) {
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            List<CertificateResponse> certificateResponses = certificateRepository.findCertificate(user.getUserId());
            return ResponseEntity.ok().body(certificateResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Something Error");
        }
    }

    private void generateCertificate(
        String volunteerName,
        String organizationName,
        String campaignName,
        Date campaignDate,
        String filePath
    ) throws Exception {
        HtmlTool htmlTool = new HtmlTool(volunteerName, organizationName, campaignName, campaignDate);
        htmlTool.export();
        ByteArrayOutputStream byteFile = htmlTool.getOutputStream();

        s3Service.putObject(
            "unidy",
            "html",
            filePath,
            byteFile.toByteArray()
        );
    }
}