package org.autoflex.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import org.autoflex.application.commands.LoginCommand;
import org.autoflex.application.dto.TokenData;
import org.autoflex.application.gateways.PasswordEncoder;
import org.autoflex.application.gateways.TokenIssuer;
import org.autoflex.application.gateways.UserRepository;
import org.autoflex.application.usecases.AuthUseCase;
import org.autoflex.common.exceptions.UnauthorizedException;
import org.autoflex.domain.User;
import org.autoflex.domain.UserRole;

@ApplicationScoped
public class AuthServiceImpl implements AuthUseCase {

  @Inject PasswordEncoder passwordEncoder;
  @Inject TokenIssuer tokenIssuer;
  @Inject UserRepository userRepository;

  @Transactional
  public TokenData authenticate(LoginCommand cmd) {
    User user =
        userRepository
            .findByEmail(cmd.email())
            .filter(User::isActive)
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

    if (!passwordEncoder.matches(cmd.password(), user.getPasswordHash())) {
      throw new UnauthorizedException("Invalid credentials");
    }

    Set<String> roles = user.getRoles().stream().map(UserRole::name).collect(Collectors.toSet());

    return tokenIssuer.issue(user.getEmail(), roles);
  }
}
