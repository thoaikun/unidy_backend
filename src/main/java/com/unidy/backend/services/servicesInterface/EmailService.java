package com.unidy.backend.services.servicesInterface;

import com.unidy.backend.domains.dto.requests.EmailDetails;

public interface EmailService {
    String sendTextMail(EmailDetails emailDetails);
    String sendAttachmentMail(EmailDetails emailDetails);
}
