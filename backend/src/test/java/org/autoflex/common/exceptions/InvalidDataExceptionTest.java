package org.autoflex.common.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InvalidDataExceptionTest {

  @Test
  void constructor_shouldSetMessage_whenMessageIsProvided() {
    InvalidDataException ex = new InvalidDataException("invalid");
    assertEquals("invalid", ex.getMessage());
  }
}
