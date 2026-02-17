package org.autoflex.common.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FieldMessageTest {

  @Test
  void constructor_shouldSetFields_whenAllArgsAreProvided() {
    FieldMessage fieldMessage = new FieldMessage("name", "required");
    assertEquals("name", fieldMessage.field);
    assertEquals("required", fieldMessage.message);
  }
}
