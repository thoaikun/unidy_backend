package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.dto.requests.CertificateRequest;
import com.unidy.backend.services.servicesInterface.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.Principal;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    @Override
    public ResponseEntity<?> createCertificate(Principal connectedUser, CertificateRequest certificateRequest) {
        try {
            String html = parseThymeleafTemplate();
            String outputFilePath = "src/main/resources/PDF/certificate.pdf";

            OutputStream outputStream = new FileOutputStream(outputFilePath);

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);

            outputStream.close();

            return ResponseEntity.ok().body(new SuccessReponse("Certificate created successfully"));
        } catch (Exception e){
//            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something went wrong while creating the certificate"));
        }
    }


    private String parseThymeleafTemplate() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("volunteerName", "Huy Thái");

        return templateEngine.process("src/main/resources/templates/Certificate.html", context);
    }
}