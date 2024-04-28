package com.unidy.backend.domains.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class VolunteerJoinCampaignId implements Serializable {
    private int userId;
    private int campaignId;
}

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "volunteer_join_campaign")
@IdClass(VolunteerJoinCampaignId.class)
public class VolunteerJoinCampaign {
    @Id
    @Column(name = "user_id")
    private int userId;

    @Id
    @Column(name = "campaign_id")
    private int campaignId;

    @Column(name = "time_join")
    private Date timeJoin;

    @Column(name = "status")
    private String status;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campaign_id", insertable = false, updatable = false)
    private Campaign campaign;
}
