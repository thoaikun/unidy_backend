package com.unidy.backend.domains.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationInformation {
    private int userId;
    private String organizationName;
    private String address;
    private String phone;
    private String email;
    private String country;
    private String image;
    private String firebaseTopic;
    private Boolean isFollow;
}
