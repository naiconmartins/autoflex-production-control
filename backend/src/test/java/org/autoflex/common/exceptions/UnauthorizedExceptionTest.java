package org.autoflex.common.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UnauthorizedExceptionTest {

  @Test
  void constructor_shouldSetMessage_whenMessageIsProvided() {
    UnauthorizedException ex = new UnauthorizedException("invalid credentials");
    assertEquals("invalid credentials", ex.getMessage());
  }

  @Test
  void constructor_shouldSetDefaultMessage_whenNoArgsAreProvided() {
    UnauthorizedException ex = new UnauthorizedException();
    assertEquals("Unauthorized", ex.getMessage());
  }
}
