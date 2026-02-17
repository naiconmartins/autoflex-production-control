package org.autoflex.common.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DatabaseExceptionTest {

  @Test
  void constructor_shouldSetMessage_whenMessageIsProvided() {
    DatabaseException ex = new DatabaseException("db error");
    assertEquals("db error", ex.getMessage());
  }
}
