package org.autoflex.application.services;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.autoflex.application.security.PasswordService;
import org.autoflex.domain.enums.UserRole;
import org.autoflex.web.dto.UserRequestDTO;
import org.autoflex.domain.entities.User;
import org.autoflex.web.dto.UserResponseDTO;
import org.autoflex.web.exceptions.*;

import java.util.Optional;

@ApplicationScoped
public class UserService {

    @Inject
    PasswordService passwordService;

    @Inject
    SecurityIdentity securityIdentity;

    @Transactional
    public UserResponseDTO insert(UserRequestDTO dto) {

        Optional<User> userOpt = User.findByEmail(dto.email);
        if (userOpt.isPresent()) throw new ConflictException("User with email " + dto.email + " already exists");

        UserRole role;

        try {
            role = UserRole.valueOf(dto.role.toUpperCase());
        } catch (Exception ex) {
            throw new InvalidDataException("Invalid role: " + dto.role);
        }

        User user = new User();
        user.setEmail(dto.email);
        user.setFirstName(dto.firstName);
        user.setLastName(dto.lastName);

        String passwordHashed = passwordService.hash(dto.password);

        user.setPasswordHash(passwordHashed);
        user.addRole(role);

        try {
            User.persist(user);
            User.flush();
        } catch (Exception ex) {
            throw new DatabaseException("Database constraint violation");
        }

        return new UserResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO getCurrentUser() {
        if (securityIdentity.isAnonymous()) {
            throw new UnauthorizedException("User is not authenticated");
        }

        String email = securityIdentity.getPrincipal().getName();

        Optional<User> userOpt = User.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        User user = userOpt.get();

        if (!user.isActive()) {
            throw new UnauthorizedException("User is not active");
        }

        return new UserResponseDTO(user);
    }

}
