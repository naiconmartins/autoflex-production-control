package org.autoflex.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class LoginResponseDTO {
    public final String accessToken;
    public final Integer expires;
    public String tokenType = "Bearer";

    public LoginResponseDTO(String accessToken, Integer expires, String tokenType) {
        this.accessToken = accessToken;
        this.expires = expires;
        this.tokenType = tokenType;
    }

    public LoginResponseDTO(String accessToken, Integer expires) {
        this.accessToken = accessToken;
        this.expires = expires;
        this.tokenType = "Bearer";
    }
}