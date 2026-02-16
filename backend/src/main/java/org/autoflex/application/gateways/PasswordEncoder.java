package org.autoflex.application.gateways;

public interface PasswordEncoder {
  String hash(String rawPassword);

  boolean matches(String rawPassword, String hashedPassword);
}
