package com.unidy.backend.domains.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VolunteerCertificatePK implements Serializable {
    private Integer volunteerId;
    private Integer certificateId;
    private Integer campaignId;

}
