package com.unidy.backend.domains.dto.responses;


import com.unidy.backend.domains.role.Role;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInformationRespond {
    private int userId;
    private String fullName;
    private String address;
    private String phone;
    private String sex;
    private Date dayOfBirth;
    private String job;
    private String workLocation;
    private Role role;
    private String image;
}
