package com.unidy.backend.domains.dto.requests;

import com.unidy.backend.domains.entity.FavoriteActivities;
import com.unidy.backend.domains.entity.relationship.CampaignType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignRequest {
    private String campaignId;
    private String title;
    private String categories;
    private String description;
    private String status;
    private String hashTag;
    private String location;
    private String donationBudget;
    private String numberVolunteer;
    private int numberRegistered;
    private String startDate;
    private String endDate;
    private String timeTakePlace;
    private List<MultipartFile> listImageFile;
    private String arrayImageLink ;
    private int owner;
}
