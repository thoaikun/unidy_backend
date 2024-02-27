package com.unidy.backend.domains.dto.responses;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class CampaignResponse {
    private Integer campaignId;
    private String title;
    private String description;
    private String categories;
    private Integer numberVolunteer;
    private Integer numberVolunteerRegistered;
    private Integer donationBudget;
    private Integer donationBudgetReceived;
    private Date startDate;
    private Date endDate;
    private Date timeTakePlace;
    private String location;
    private String status;
    private String createDate;
    private String updateDate;
    private Integer updateBy;
    private Integer ownerId;
    private String ownerName;
    private String ownerProfileImage;
    private String hashTag;
    private String linkImage;
}
