package org.autoflex.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.autoflex.application.commands.UserCommand;
import org.autoflex.application.gateways.PasswordEncoder;
import org.autoflex.application.gateways.UserRepository;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.DatabaseException;
import org.autoflex.common.exceptions.InvalidDataException;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.domain.User;
import org.autoflex.domain.UserRole;
import org.autoflex.fixtures.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

  @Mock PasswordEncoder hasher;
  @Mock UserRepository userRepository;

  @InjectMocks UserServiceImpl userService;

  private UserCommand validCommand;

  @BeforeEach
  void setUp() {
    validCommand = UserFixture.createValidUserCommand();
  }

  @Test
  void insert_shouldCreateUser_whenValidCommand() {
    when(userRepository.findByEmail(validCommand.email())).thenReturn(Optional.empty());
    when(hasher.hash(validCommand.password())).thenReturn("hashed-password");
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

    User result = userService.insert(validCommand);

    assertNotNull(result);
    assertEquals(validCommand.email(), result.getEmail());
    assertEquals(validCommand.firstName(), result.getFirstName());
    assertEquals(validCommand.lastName(), result.getLastName());
    assertEquals("hashed-password", result.getPasswordHash());
    assertTrue(result.getRoles().contains(UserRole.USER));

    verify(userRepository).findByEmail(validCommand.email());
    verify(hasher).hash(validCommand.password());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void insert_shouldCreateUser_whenRoleIsLowerCase() {
    UserCommand cmd = UserFixture.createUserCommandWithRole("admin");

    when(userRepository.findByEmail(cmd.email())).thenReturn(Optional.empty());
    when(hasher.hash(cmd.password())).thenReturn("hashed-password");
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

    User result = userService.insert(cmd);

    assertTrue(result.getRoles().contains(UserRole.ADMIN));
  }

  @Test
  void insert_shouldThrowConflictException_whenEmailAlreadyExists() {
    when(userRepository.findByEmail(validCommand.email()))
        .thenReturn(Optional.of(UserFixture.createValidUser()));

    ConflictException ex =
        assertThrows(ConflictException.class, () -> userService.insert(validCommand));

    assertEquals("User with email email@test.com already exists", ex.getMessage());
    verify(hasher, never()).hash(any());
    verify(userRepository, never()).save(any());
  }

  @Test
  void insert_shouldThrowInvalidDataException_whenRoleIsInvalid() {
    UserCommand invalidRoleCmd = UserFixture.createUserCommandWithRole("MANAGER");

    when(userRepository.findByEmail(invalidRoleCmd.email())).thenReturn(Optional.empty());

    InvalidDataException ex =
        assertThrows(InvalidDataException.class, () -> userService.insert(invalidRoleCmd));

    assertEquals("Invalid role: MANAGER", ex.getMessage());
    verify(hasher, never()).hash(any());
    verify(userRepository, never()).save(any());
  }

  @Test
  void insert_shouldThrowDatabaseException_whenSaveFails() {
    when(userRepository.findByEmail(validCommand.email())).thenReturn(Optional.empty());
    when(hasher.hash(validCommand.password())).thenReturn("hashed-password");
    when(userRepository.save(any(User.class)))
        .thenThrow(new RuntimeException("constraint violation"));

    DatabaseException ex =
        assertThrows(DatabaseException.class, () -> userService.insert(validCommand));

    assertEquals("Database constraint violation", ex.getMessage());
  }

  @Test
  void insert_shouldThrowException_whenHashFails() {
    when(userRepository.findByEmail(validCommand.email())).thenReturn(Optional.empty());
    when(hasher.hash(validCommand.password())).thenThrow(new RuntimeException("hash error"));

    RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.insert(validCommand));

    assertEquals("hash error", ex.getMessage());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void findByEmail_shouldReturnUser_whenUserExistsAndIsActive() {
    User activeUser = UserFixture.createValidUser();
    when(userRepository.findByEmail("email@test.com")).thenReturn(Optional.of(activeUser));

    User result = userService.findByEmail("email@test.com");

    assertNotNull(result);
    assertEquals("email@test.com", result.getEmail());
    assertTrue(result.isActive());
  }

  @Test
  void findByEmail_shouldThrowResourceNotFoundException_whenUserDoesNotExist() {
    when(userRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(
            ResourceNotFoundException.class, () -> userService.findByEmail("notfound@test.com"));

    assertEquals("User not found or inactive", ex.getMessage());
  }

  @Test
  void findByEmail_shouldThrowResourceNotFoundException_whenUserIsInactive() {
    User inactiveUser = UserFixture.createInactiveUser();
    when(userRepository.findByEmail("email@test.com")).thenReturn(Optional.of(inactiveUser));

    ResourceNotFoundException ex =
        assertThrows(
            ResourceNotFoundException.class, () -> userService.findByEmail("email@test.com"));

    assertEquals("User not found or inactive", ex.getMessage());
  }
}
