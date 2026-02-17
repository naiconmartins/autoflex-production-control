package org.autoflex.common.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ResourceNotFoundExceptionTest {

  @Test
  void constructor_shouldSetMessage_whenMessageIsProvided() {
    ResourceNotFoundException ex = new ResourceNotFoundException("not found");
    assertEquals("not found", ex.getMessage());
  }
}
