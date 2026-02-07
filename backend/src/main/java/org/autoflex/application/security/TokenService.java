package org.autoflex.application.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import org.autoflex.domain.entities.User;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

@ApplicationScoped
public class TokenService {

    private static final Duration TOKEN_DURATION = Duration.ofHours(2);

    public TokenData issue(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(TOKEN_DURATION);

        String token = Jwt.issuer("autoflex")
                .subject(user.getEmail())
                .expiresAt(expiresAt.getEpochSecond())
                .groups(user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()))
                .sign();

        return new TokenData(token, (int) TOKEN_DURATION.toSeconds());
    }

    @Getter
    public static class TokenData {
        private final String token;
        private final Integer expiresIn;

        public TokenData(String token, Integer expiresIn) {
            this.token = token;
            this.expiresIn = expiresIn;
        }

    }
}