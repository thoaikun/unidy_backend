package com.unidy.backend.domains.dto.responses;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListVolunteerResponse {
    private Integer userId;
    private String fullName;
    private String address;
    private String email;
    private Date timeJoin;
    private String status;
    private Integer campaignId;
    private String description;
}
