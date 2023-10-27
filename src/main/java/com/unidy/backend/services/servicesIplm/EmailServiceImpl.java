package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.domains.dto.requests.EmailDetails;
import com.unidy.backend.services.servicesInterface.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    public final JavaMailSender javaMailSender;
    @Value("{spring.mail.username}")
    String sender = "unidyteam@gmail.com";

    @Override
    public String sendTextMail(EmailDetails mail) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(mail.getRecipient());
            mailMessage.setText(mail.getMsgBody());
            mailMessage.setSubject(mail.getSubject());

            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        }
        catch (Exception e) {
            return "Error while Sending Mail";
        }
    }

    @Override
    public String sendAttachmentMail(EmailDetails emailDetails) {
        return null;
    }
}
