package org.autoflex.adapters.inbound.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserRequestDTO {

  @Email(message = "Invalid email format")
  @NotNull(message = "Email address must not be null")
  @NotBlank(message = "Email is required")
  public String email;

  @NotNull(message = "Password must not be null")
  @NotBlank(message = "Password is required")
  public String password;

  @NotNull(message = "First name must not be null")
  @NotBlank(message = "First name is required")
  public String firstName;

  @NotNull(message = "Last name must not be null")
  @NotBlank(message = "Last name is required")
  public String lastName;

  @NotNull(message = "Role must not be null")
  @NotBlank(message = "Role name is required")
  public String role;

  public UserRequestDTO() {}

  public UserRequestDTO(
      String email, String password, String firstName, String lastName, String role) {
    this.email = email;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.role = role;
  }
}
