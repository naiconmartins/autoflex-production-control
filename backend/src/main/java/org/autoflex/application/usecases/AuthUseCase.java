package org.autoflex.application.usecases;

import org.autoflex.application.commands.LoginCommand;
import org.autoflex.application.dto.TokenData;

public interface AuthUseCase {

  TokenData authenticate(LoginCommand cmd);
}
