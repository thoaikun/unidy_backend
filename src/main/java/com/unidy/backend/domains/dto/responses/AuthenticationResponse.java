package com.unidy.backend.domains.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unidy.backend.domains.role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("refresh_token")
  private String refreshToken;
  @JsonProperty("isChosenFavorite")
  private Boolean isChosenFavorite;
  @JsonProperty("role")
  private Role role;

}
