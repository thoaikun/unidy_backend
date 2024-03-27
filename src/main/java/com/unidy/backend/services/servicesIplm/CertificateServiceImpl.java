package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.S3.S3Service;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.dto.requests.CertificateRequest;
import com.unidy.backend.domains.dto.responses.CertificateResponse;
import com.unidy.backend.domains.entity.*;
import com.unidy.backend.repositories.*;
import com.unidy.backend.services.servicesInterface.CertificateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {
    private final S3Service s3Service;
    private final CertificateRepository certificateRepository;
    private final VolunteerCertificateRepository volunteerCertificateRepository;
    private final OrganizationRepository organizationRepository;
    private final VolunteerRepository volunteerRepository;
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public ResponseEntity<?> createCertificate(Principal connectedUser, CertificateRequest certificateRequest) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Optional<Organization> organization = organizationRepository.findByUserId(user.getUserId());
        Volunteer volunteer = volunteerRepository.findByUserId(certificateRequest.getUserId());
        if (organization.isEmpty()){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Can't find organization"));
        }
        try {
            String html = parseThymeleafTemplate(certificateRequest);
            ByteArrayOutputStream byteFile = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(byteFile);
            byteFile.close();

            s3Service.putObject(
                    "unidy",
                    "pdf",
                    "certificate/%s.pdf".formatted(certificateRequest.getUserId()+"_"+ certificateRequest.getCampaignId()+"_"+ organization.get().getOrganizationId()),
                    byteFile.toByteArray()
            );

            String certificateLink = "https://unidy.s3.ap-southeast-1.amazonaws.com/certificate/%s.pdf".formatted(certificateRequest.getUserId()+"_"+ certificateRequest.getCampaignId()+"_"+ organization.get().getOrganizationId());
            Certificate certificate = Certificate.builder().file(certificateLink).build();
            certificateRepository.save(certificate);

            VolunteerCertificate volunteerCertificate = VolunteerCertificate.builder()
                    .volunteerId(volunteer.getVolunteerId())
                    .campaignId(certificateRequest.getCampaignId())
                    .certificateId(certificate.getCertificateId())
                    .build();

            volunteerCertificateRepository.save(volunteerCertificate);


            return ResponseEntity.ok().body(new SuccessReponse("Certificate created successfully"));
        } catch (Exception e){
            s3Service.deleteObject("unidy",
                    "pdf",
                    "certificate/%s.pdf".formatted(certificateRequest.getUserId()+"_"+ certificateRequest.getCampaignId()+"_"+ organization.get().getOrganizationId())
                    );
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something went wrong while creating the certificate"));
        }
    }

    @Override
    public ResponseEntity<?> getCertificate(Principal connectedUser) {
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            List<CertificateResponse> certificateResponses = certificateRepository.findCertificate(user.getUserId());
            return ResponseEntity.ok().body(certificateResponses);
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Something Error");
        }
    }


    private String parseThymeleafTemplate(CertificateRequest certificateRequest) {
        User user = userRepository.findByUserId(certificateRequest.getUserId());
        Campaign campaign = campaignRepository.findCampaignByCampaignId(certificateRequest.getCampaignId());
        Optional<Organization> organization = organizationRepository.findByUserId(campaign.getOwner());
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("certificate/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("volunteerName", user.getFullName());
        context.setVariable("campaignName", campaign.getDescription());
        context.setVariable("organizationName", organization.get().getOrganizationName());
        context.setVariable("logo", "linkLogo");
        return templateEngine.process("certificate", context);
    }
}