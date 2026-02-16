package org.autoflex.infraestructure.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BcryptPasswordHasherTest {

  BcryptPasswordHasher bcryptPasswordHasher = new BcryptPasswordHasher();

  @Test
  void passwordMatches_shouldReturnTrue_whenCredentialsAreValid() {
    String raw = "secret123";
    String hashed = bcryptPasswordHasher.hash(raw);

    assertTrue(bcryptPasswordHasher.matches(raw, hashed));
  }

  @Test
  void passwordMatches_shouldReturnFalse_whenCredentialsAreInvalid() {
    String raw = "secret123";
    String hashed = bcryptPasswordHasher.hash(raw);

    assertFalse(bcryptPasswordHasher.matches("wrong_password", hashed));
  }
}
