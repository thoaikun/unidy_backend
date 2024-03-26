package com.unidy.backend.domains.entity;

import com.unidy.backend.domains.dto.VolunteerCertificatePK;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(VolunteerCertificatePK.class)
@Table(name = "volunteer_certificate")
public class VolunteerCertificate {
    @Id
    @Column(name = "volunteer_id")
    private Integer volunteerId;

    @Id
    @Column(name = "certificate_id")
    private Integer certificateId;

    @Id
    @Column(name = "campaign_id")
    private Integer campaignId;
}
