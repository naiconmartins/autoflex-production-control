package org.autoflex.web.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginResponseDTO {

    public final String accessToken;
    public String tokenType = "Bearer";
}
