package org.autoflex.infraestructure.security;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.autoflex.application.gateways.PasswordEncoder;

@ApplicationScoped
public class BcryptPasswordHasher implements PasswordEncoder {

  @Override
  public String hash(String rawPassword) {
    return BcryptUtil.bcryptHash(rawPassword);
  }

  @Override
  public boolean matches(String rawPassword, String hashedPassword) {
    return BcryptUtil.matches(rawPassword, hashedPassword);
  }
}
