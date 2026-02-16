package org.autoflex.infraestructure.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import org.autoflex.application.dto.TokenData;
import org.autoflex.application.gateways.TokenIssuer;

@ApplicationScoped
public class JwtTokenIssuerAdapter implements TokenIssuer {

  private static final Duration TOKEN_DURATION = Duration.ofHours(2);

  @Override
  public TokenData issue(String subject, Set<String> roles) {
    Instant expiresAt = Instant.now().plus(TOKEN_DURATION);

    String token =
        Jwt.issuer("autoflex")
            .subject(subject)
            .expiresAt(expiresAt.getEpochSecond())
            .groups(roles)
            .sign();

    return new TokenData(token, (int) TOKEN_DURATION.toSeconds());
  }
}
