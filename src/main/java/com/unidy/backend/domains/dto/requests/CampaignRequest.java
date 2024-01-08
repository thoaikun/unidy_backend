package com.unidy.backend.domains.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignRequest {
    private String campaignId;
    private String content;
    private String status;
    private String hashTag;
    private int numOfVolunteer;
    private String startDate;
    private String endDate;
    private List<MultipartFile> listImageFile;
    private String arrayImageLink ;
}
