package com.unidy.backend.domains.dto.responses;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
public class ListVolunteerResponse {
    private Integer userId;
    private String fullName;
    private Date timeJoin;
    private String status;
    private Integer campaignId;
    private String linkImage;

    public ListVolunteerResponse(Integer userId, String fullName, Date timeJoin, String status, Integer campaignId, String linkImages) {
        this.userId = userId;
        this.fullName = fullName;
        this.timeJoin = timeJoin;
        this.status = status;
        this.campaignId = campaignId;
        this.linkImage = linkImages == null ? null : "https://unidy.s3.ap-southeast-1.amazonaws.com/" + "profile-images/" + userId + "/" + linkImages;
    }
}
