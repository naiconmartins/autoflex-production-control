package org.autoflex.common.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ForbiddenExceptionTest {

  @Test
  void constructor_shouldSetMessage_whenMessageIsProvided() {
    ForbiddenException ex = new ForbiddenException("forbidden");
    assertEquals("forbidden", ex.getMessage());
  }
}
