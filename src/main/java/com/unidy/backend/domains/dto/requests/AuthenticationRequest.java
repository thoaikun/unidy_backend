package com.unidy.backend.domains.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
  @NotNull
  @Email(message ="Email không hợp lệ")
  private String email;
  @NotNull
  @Min(1)
  String password;
}
