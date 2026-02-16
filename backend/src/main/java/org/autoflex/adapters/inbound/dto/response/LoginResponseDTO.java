package org.autoflex.adapters.inbound.dto.response;

import lombok.Getter;

@Getter
public class LoginResponseDTO {
  public final String accessToken;
  public final Integer expires;
  public String tokenType = "Bearer";

  public LoginResponseDTO(String accessToken, Integer expires) {
    this.accessToken = accessToken;
    this.expires = expires;
    this.tokenType = "Bearer";
  }
}
