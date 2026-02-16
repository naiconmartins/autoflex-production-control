package org.autoflex.application.usecases;

import org.autoflex.application.commands.InsertUserCommand;
import org.autoflex.domain.User;

public interface UserUseCases {
  User insert(InsertUserCommand user);

  User findByEmail(String email);
}
