package org.autoflex.fixtures;

import java.util.Set;
import org.autoflex.application.commands.UserCommand;
import org.autoflex.application.commands.LoginCommand;
import org.autoflex.adapters.inbound.dto.request.UserRequestDTO;
import org.autoflex.domain.User;
import org.autoflex.domain.UserRole;

public class UserFixture {

  public static User createValidUser() {
    User user = new User();
    user.setEmail("email@test.com");
    user.setPasswordHash("hash_protegido");
    user.setActive(true);
    user.setRoles(Set.of(UserRole.USER));
    return user;
  }

  public static User createInactiveUser() {
    User user = createValidUser();
    user.setActive(false);
    return user;
  }

  public static UserCommand createValidUserCommand() {
    return new UserCommand("email@test.com", "secret123", "Amanda", "Ribeiro", "USER");
  }

  public static UserCommand createUserCommandWithRole(String role) {
    return new UserCommand("email@test.com", "secret123", "Amanda", "Ribeiro", role);
  }

  public static LoginCommand createValidLoginCommand() {
    return new LoginCommand("email@test.com", "secret123");
  }

  public static UserRequestDTO createValidUserRequestDTO() {
    return new UserRequestDTO("email@test.com", "secret123", "Amanda", "Ribeiro", "USER");
  }

  public static UserRequestDTO createUserRequestWithRole(String role) {
    return new UserRequestDTO("email@test.com", "secret123", "Amanda", "Ribeiro", role);
  }
}
