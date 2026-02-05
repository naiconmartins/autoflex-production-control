package org.autoflex.application.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.autoflex.domain.entities.User;

import java.time.Duration;
import java.util.stream.Collectors;

@ApplicationScoped
public class TokenService {

    public String issue(User user) {
        return Jwt.issuer("autoflex")
                .subject(user.getEmail())
                .expiresIn(Duration.ofHours(2))
                .groups(user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()))
                .sign();
    }
}
