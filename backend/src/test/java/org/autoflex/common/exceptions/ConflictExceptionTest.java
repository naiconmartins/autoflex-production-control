package org.autoflex.common.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ConflictExceptionTest {

  @Test
  void constructor_shouldSetMessage_whenMessageIsProvided() {
    ConflictException ex = new ConflictException("conflict");
    assertEquals("conflict", ex.getMessage());
  }
}
