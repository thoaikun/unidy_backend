package com.unidy.backend.domains.dto.requests;

import com.unidy.backend.domains.role.Role;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterRequest {

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
}
