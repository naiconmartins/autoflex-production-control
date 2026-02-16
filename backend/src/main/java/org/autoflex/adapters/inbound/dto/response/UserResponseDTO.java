package org.autoflex.adapters.inbound.dto.response;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.autoflex.domain.User;

@Getter
@Setter
public class UserResponseDTO {
  public Long id;
  public String email;
  public String firstName;
  public String lastName;
  public Set<String> roles;
  public Boolean active;
  public Instant createdAt;

  public UserResponseDTO() {}

  public UserResponseDTO(User user) {
    this.id = user.getId();
    this.email = user.getEmail();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
    this.active = user.isActive();
    this.createdAt = user.getCreatedAt();
  }

  public UserResponseDTO(
      Long id,
      String email,
      String firstName,
      String lastName,
      Set<String> roles,
      Boolean active,
      Instant createdAt) {
    this.id = id;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.roles = roles;
    this.active = active;
    this.createdAt = createdAt;
  }
}
