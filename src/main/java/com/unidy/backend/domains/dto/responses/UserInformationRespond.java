package com.unidy.backend.domains.dto.responses;


import com.unidy.backend.domains.role.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
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
}
