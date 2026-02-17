package org.autoflex.application.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TokenDataTest {

  @Test
  void constructor_shouldSetFields_whenCreatedWithValues() {
    TokenData dto = new TokenData("jwt-token", 3600);

    assertEquals("jwt-token", dto.token());
    assertEquals(3600, dto.expiresIn());
  }
}
