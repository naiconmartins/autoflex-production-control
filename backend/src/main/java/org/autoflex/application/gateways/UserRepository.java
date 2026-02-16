package org.autoflex.application.gateways;

import java.util.Optional;
import org.autoflex.domain.User;

public interface UserRepository {

  Optional<User> findByEmail(String email);

  User save(User user);
}
