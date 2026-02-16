package org.autoflex.fixtures;

import java.util.Set;
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
}
