package com.unidy.backend.domains.dto.requests;

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
    private int numOfVolunteer;
    private int numberRegistered;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date endDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date timeTakePlace;
    private List<MultipartFile> listImageFile;
    private String arrayImageLink ;
    private int owner;
}
