package com.unidy.backend.domains.dto.requests;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CertificateRequest {
    private int volunteerId;
    private int campaignId;
    private int organizationId;
}
