package org.autoflex.application.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.autoflex.application.commands.UserCommand;
import org.autoflex.common.exceptions.InvalidDataException;
import org.autoflex.fixtures.UserFixture;
import org.junit.jupiter.api.Test;

public class UserCommandTest {

  @Test
  void shouldThrowInvalidDataException_whenEmailIsNull() {
    assertInvalidCommand(null, "secret123", "Amanda", "Ribeiro", "USER", "Email is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenEmailIsEmpty() {
    assertInvalidCommand("", "secret123", "Amanda", "Ribeiro", "USER", "Email is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenEmailIsInvalid() {
    assertInvalidCommand(
        "invalid-email", "secret123", "Amanda", "Ribeiro", "USER", "Invalid email format");
  }

  @Test
  void shouldThrowInvalidDataException_whenPasswordIsNull() {
    assertInvalidCommand(
        "email@test.com", null, "Amanda", "Ribeiro", "USER", "Password is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenPasswordIsEmpty() {
    assertInvalidCommand("email@test.com", "", "Amanda", "Ribeiro", "USER", "Password is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenFirstNameIsNull() {
    assertInvalidCommand(
        "email@test.com", "secret123", null, "Ribeiro", "USER", "First name is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenFirstNameIsEmpty() {
    assertInvalidCommand(
        "email@test.com", "secret123", "", "Ribeiro", "USER", "First name is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenLastNameIsNull() {
    assertInvalidCommand(
        "email@test.com", "secret123", "Amanda", null, "USER", "Last name is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenLastNameIsEmpty() {
    assertInvalidCommand(
        "email@test.com", "secret123", "Amanda", "", "USER", "Last name is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenRoleIsNull() {
    assertInvalidCommand(
        "email@test.com", "secret123", "Amanda", "Ribeiro", null, "Role name is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenRoleIsEmpty() {
    assertInvalidCommand(
        "email@test.com", "secret123", "Amanda", "Ribeiro", "", "Role name is required");
  }

  @Test
  void shouldCreateCommand_whenValidData() {
    assertDoesNotThrow(
        () -> {
          UserCommand cmd = UserFixture.createValidUserCommand();
          createCommand(cmd.email(), cmd.password(), cmd.firstName(), cmd.lastName(), cmd.role());
        });
  }

  private static void assertInvalidCommand(
      String email,
      String password,
      String firstName,
      String lastName,
      String role,
      String expectedMessage) {
    InvalidDataException ex =
        assertThrows(
            InvalidDataException.class,
            () -> createCommand(email, password, firstName, lastName, role));

    assertEquals(expectedMessage, ex.getMessage());
  }

  private static UserCommand createCommand(
      String email, String password, String firstName, String lastName, String role) {
    return new UserCommand(email, password, firstName, lastName, role);
  }
}
