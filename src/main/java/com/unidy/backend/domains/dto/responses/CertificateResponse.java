package com.unidy.backend.domains.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateResponse {
    private int certificateId;
    private int campaignId;
    private String campaignName;
    private int organizationId;
    private String organizationName;
    private String certificateLink;
}
