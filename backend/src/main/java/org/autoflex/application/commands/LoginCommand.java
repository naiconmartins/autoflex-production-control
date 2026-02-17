package org.autoflex.application.commands;

import java.util.regex.Pattern;
import org.autoflex.common.exceptions.InvalidDataException;

public record LoginCommand(String email, String password) {

  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

  public LoginCommand {
    if (email == null || email.isBlank()) {
      throw new InvalidDataException("Email is required");
    }
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new InvalidDataException("Invalid email format");
    }
    if (password == null || password.isBlank()) {
      throw new InvalidDataException("Password is required");
    }
  }
}
