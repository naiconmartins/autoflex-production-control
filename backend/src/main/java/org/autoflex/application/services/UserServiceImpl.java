package org.autoflex.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.autoflex.application.commands.InsertUserCommand;
import org.autoflex.application.gateways.PasswordEncoder;
import org.autoflex.application.gateways.UserRepository;
import org.autoflex.application.usecases.UserUseCase;
import org.autoflex.common.exceptions.*;
import org.autoflex.domain.User;
import org.autoflex.domain.UserRole;

@ApplicationScoped
public class UserServiceImpl implements UserUseCase {

  @Inject PasswordEncoder hasher;
  @Inject UserRepository userRepository;

  @Override
  @Transactional
  public User insert(InsertUserCommand dto) {

    Optional<User> userOpt = userRepository.findByEmail(dto.email());

    if (userOpt.isPresent())
      throw new ConflictException("User with email " + dto.email() + " already exists");

    UserRole role;

    try {
      role = UserRole.valueOf(dto.role().toUpperCase());
    } catch (Exception ex) {
      throw new InvalidDataException("Invalid role: " + dto.role());
    }

    User user = new User();
    user.setEmail(dto.email());
    user.setFirstName(dto.firstName());
    user.setLastName(dto.lastName());

    String passwordHashed = hasher.hash(dto.password());

    user.setPasswordHash(passwordHashed);
    user.getRoles().add(role);

    try {
      userRepository.save(user);
    } catch (Exception ex) {
      throw new DatabaseException("Database constraint violation");
    }

    return user;
  }

  @Override
  @Transactional
  public User findByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .filter(User::isActive)
        .orElseThrow(() -> new ResourceNotFoundException("User not found or inactive"));
  }
}
