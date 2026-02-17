package org.autoflex.application.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.autoflex.application.commands.LoginCommand;
import org.autoflex.common.exceptions.InvalidDataException;
import org.autoflex.fixtures.UserFixture;
import org.junit.jupiter.api.Test;

public class LoginCommandTest {

  @Test
  void shouldThrowInvalidDataException_whenEmailIsNull() {
    assertInvalidCommand(null, "secret123", "Email is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenEmailIsEmpty() {
    assertInvalidCommand("", "secret123", "Email is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenEmailIsInvalid() {
    assertInvalidCommand("invalid-email", "secret123", "Invalid email format");
  }

  @Test
  void shouldThrowInvalidDataException_whenPasswordIsNull() {
    assertInvalidCommand("email@test.com", null, "Password is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenPasswordIsEmpty() {
    assertInvalidCommand("email@test.com", "", "Password is required");
  }

  @Test
  void shouldCreateCommand_whenValidData() {
    assertDoesNotThrow(
        () -> {
          LoginCommand cmd = UserFixture.createValidLoginCommand();
          createCommand(cmd.email(), cmd.password());
        });
  }

  private static void assertInvalidCommand(String email, String password, String expectedMessage) {
    InvalidDataException ex =
        assertThrows(InvalidDataException.class, () -> createCommand(email, password));

    assertEquals(expectedMessage, ex.getMessage());
  }

  private static LoginCommand createCommand(String email, String password) {
    return new LoginCommand(email, password);
  }
}
