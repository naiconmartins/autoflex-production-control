package org.autoflex.application.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.autoflex.web.dto.LoginRequestDTO;
import org.autoflex.domain.entities.User;
import org.autoflex.web.dto.LoginResponseDTO;
import org.autoflex.web.exceptions.UnauthorizedException;

import java.util.Optional;

@ApplicationScoped
public class AuthService {

    @Inject
    PasswordService passwordService;
    @Inject
    TokenService tokenService;

    @Transactional
    public LoginResponseDTO authenticate(LoginRequestDTO dto) {
        Optional<User> userOpt = User.findByEmail(dto.email);

        if (userOpt.isEmpty()) throw new UnauthorizedException();

        User user = userOpt.get();

        if (!user.isActive()) throw new UnauthorizedException();
        if (!passwordService.matches(dto.password, user.getPasswordHash()))
            throw new UnauthorizedException();

        String token = tokenService.issue(user);

        return new LoginResponseDTO(token);
    }
}
