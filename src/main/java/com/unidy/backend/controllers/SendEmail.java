package com.unidy.backend.controllers;


import com.unidy.backend.domains.dto.requests.EmailDetails;
import com.unidy.backend.services.servicesInterface.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/send-email")

public class SendEmail {
    public final EmailService emailService ;


    @PostMapping("/send-text-mail")
    public ResponseEntity<String> sendTextMail(@RequestBody EmailDetails email){
        try {
            return ResponseEntity.ok().body(emailService.sendTextMail(email));
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Send email fail");
        }
    }

    @PostMapping("send-attachment-email")
    private ResponseEntity<String> sendAttachmentEmail(EmailDetails email) {
        try {
            return ResponseEntity.ok().body(emailService.sendTextMail(email));
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Send email fail");
        }
    }
}
