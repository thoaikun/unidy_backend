package com.unidy.backend.domains.dto.requests;

import com.unidy.backend.domains.role.Role;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterRequest {
  @NotNull(message = "Họ và tên không thể trống")
  private String fullName;
  private String address;
  @NotNull
  private Date dayOfBirth;
  private String sex;
  @NotNull
  @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại không hợp lệ")
  private String phone;
  @Email(message = "Email không hợp lệ")
  private String email;
  private String job;
  private String workLocation;

  @Size(min = 8, message = "Password phải từ 8 kí tự trở lên")
  private String password;
  private Role role;
}
