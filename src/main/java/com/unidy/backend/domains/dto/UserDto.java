package com.unidy.backend.domains.dto;

import com.unidy.backend.domains.role.Role;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Integer userId;
    private String fullName;
    private String address;
    private Date dayOfBirth;
    private String sex;
    private String phone;
    private String email;
    private String job;
    private String workLocation;
    private String password;
    private Role role;
    private String linkImage;
}
