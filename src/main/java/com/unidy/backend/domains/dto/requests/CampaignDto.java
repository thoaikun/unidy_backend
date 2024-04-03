package com.unidy.backend.domains.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignDto {
    private String title;
    private String categories;
    private String description;
    private String status;
    private String hashTag;
    private String location;
    private int numberVolunteer;
    private int budgetTarget;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date endDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date timeTakePlace;
}
