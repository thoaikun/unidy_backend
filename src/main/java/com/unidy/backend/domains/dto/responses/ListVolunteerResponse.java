package com.unidy.backend.domains.dto.responses;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
public class ListVolunteerResponse {
    private Integer userId;
    private String fullName;
    private Integer age;
    private String job;
    private String workLocation;
    private Date timeJoin;
    private String status;
    private Integer campaignId;
    private String linkImage;

    public ListVolunteerResponse(Integer userId, String fullName, Integer age, String job, String workLocation, Date timeJoin, String status, Integer campaignId, String linkImages) {
        this.userId = userId;
        this.fullName = fullName;
        this.age = age;
        this.job = job;
        this.workLocation = workLocation;
        this.timeJoin = timeJoin;
        this.status = status;
        this.campaignId = campaignId;
        this.linkImage = linkImages;
    }
}
