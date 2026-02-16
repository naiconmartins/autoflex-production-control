package org.autoflex.adapters.inbound.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

  @Email(message = "Invalid email format")
  @NotBlank(message = "Email is required")
  public String email;

  @NotBlank(message = "Password is required")
  public String password;

  public LoginRequestDTO(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
