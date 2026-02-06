package org.autoflex.application.security;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class PasswordServiceTest {
    @Inject
    PasswordService passwordService;

    private final String RAW_PASSWORD = "secret_password_2026";

    @Test
    void hash_shouldGenerateValidBcryptHash() {
        String hash = passwordService.hash(RAW_PASSWORD);

        Assertions.assertNotNull(hash);
        Assertions.assertNotEquals(RAW_PASSWORD, hash);
        Assertions.assertTrue(hash.startsWith("$2"), "Bcrypt hash should start with $2");
    }

    @Test
    void matches_shouldValidateCorrectAndIncorrectPasswords() {
        String hash = passwordService.hash(RAW_PASSWORD);

        Assertions.assertTrue(passwordService.matches(RAW_PASSWORD, hash), "Should match correct password");
        Assertions.assertFalse(passwordService.matches("wrong_pass", hash), "Should not match incorrect password");
    }
}
