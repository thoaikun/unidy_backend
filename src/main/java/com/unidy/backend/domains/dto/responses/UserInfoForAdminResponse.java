package com.unidy.backend.domains.dto.responses;

import com.unidy.backend.domains.role.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoForAdminResponse {
    private Integer userId;
    private String fullName;
    private String address;
    private Date dayOfBirth;
    private String sex;
    private String phone;
    private String email;
    private String job;
    private String workLocation;
    private Role role;
    private Boolean isBlock;
    String linkImage;
}
