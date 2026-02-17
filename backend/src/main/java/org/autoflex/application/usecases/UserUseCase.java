package org.autoflex.application.usecases;

import org.autoflex.application.commands.UserCommand;
import org.autoflex.domain.User;

public interface UserUseCase {
  User insert(UserCommand user);

  User findByEmail(String email);
}
