package com.unidy.backend.domains.dto.responses;

import com.unidy.backend.domains.role.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private Boolean followed;
}
