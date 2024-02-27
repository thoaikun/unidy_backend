package com.unidy.backend.domains.entity.relationship;

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
@Table(name = "campaign_type")
public class CampaignType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "campaign_id")
    private Integer campaignId;

    @Column(name = "community_type")
    private Double communityType;

    @Column(name = "education_type")
    private Double education;

    @Column(name = "research_writing_editing")
    private Double research;

    @Column(name = "help_other")
    private Double helpOther;

    @Column(name = "environment")
    private Double environment;

    @Column(name = "healthy")
    private Double healthy;

    @Column(name = "emergency_preparedness")
    private Double emergencyPreparedness;
}
