package org.autoflex.application.commands;

import java.util.regex.Pattern;
import org.autoflex.common.exceptions.InvalidDataException;

public record UserCommand(
    String email, String password, String firstName, String lastName, String role) {

  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

  public UserCommand {
    if (email == null || email.isBlank()) {
      throw new InvalidDataException("Email is required");
    }
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new InvalidDataException("Invalid email format");
    }
    if (password == null || password.isBlank()) {
      throw new InvalidDataException("Password is required");
    }
    if (firstName == null || firstName.isBlank()) {
      throw new InvalidDataException("First name is required");
    }
    if (lastName == null || lastName.isBlank()) {
      throw new InvalidDataException("Last name is required");
    }
    if (role == null || role.isBlank()) {
      throw new InvalidDataException("Role name is required");
    }
  }
}
