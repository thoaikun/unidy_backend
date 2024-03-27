package com.unidy.backend.domains.dto.requests;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CertificateRequest {
    private int userId;
    private int campaignId;
}
