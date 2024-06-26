package com.unidy.backend.domains.entity;

import com.unidy.backend.domains.entity.relationship.CampaignType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "campaign")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_id")
    private Integer campaignId;

    @Column(name= "title")
    private String title;

    @Column(name= "content")
    private String description;

    @Column(name = "numbers_volunteer")
    private Integer numberVolunteer;

    @Column(name = "number_volunteer_registered")
    private Integer numberVolunteerRegistered;

    @Column(name = "donation_budget")
    private Integer donationBudget;

    @Column(name = "donation_budget_received")
    private Integer donationBudgetReceived;

    @Column(name = "start_day")
    private Date startDate;

    @Column(name = "end_day")
    private Date endDate;

    @Column(name = "time_take_place")
    private Date timeTakePlace;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    private String status;

    @Column(name = "create_day")
    @Builder.Default
    private Timestamp createDate = new Timestamp(System.currentTimeMillis());

    @Column(name = "update_day")
    @Builder.Default
    private Timestamp updateDate = new Timestamp(System.currentTimeMillis());

    @Column(name = "update_by")
    private Integer updateBy;

    @Column(name = "owner")
    private Integer owner;

    @Column(name = "hash_tag")
    private String hashTag;

    @Column(name = "link_image")
    private String link_image;

    @JoinColumn(name = "campaign_id", referencedColumnName = "campaign_id", insertable = false, updatable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private CampaignType campaignType;

    @JoinColumn(name = "owner", referencedColumnName = "user_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Organization organization;
}
