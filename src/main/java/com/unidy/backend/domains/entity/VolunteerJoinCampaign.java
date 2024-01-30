package com.unidy.backend.domains.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "volunteer_join_campaign")
public class VolunteerJoinCampaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "volunteer_id")
    private int volunteerId;

    @Column(name = "campaign_id")
    private int campaignId;

    @Column(name = "time_join")
    private Date timeJoin;

    @Column(name = "status")
    private String status;
}
