package org.autoflex.common.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class ValidationErrorTest {

  @Test
  void constructor_shouldSetFields_whenAllArgsAreProvided() {
    Instant now = Instant.now();
    ValidationError validationError = new ValidationError(now, 422, "Invalid data", "/products");

    assertEquals(now, validationError.timestamp);
    assertEquals(422, validationError.status);
    assertEquals("Invalid data", validationError.error);
    assertEquals("/products", validationError.path);
    assertNotNull(validationError.errors);
  }

  @Test
  void addError_shouldAddFieldMessage_whenFieldAndMessageAreProvided() {
    ValidationError validationError = new ValidationError();

    validationError.addError("name", "required");

    assertEquals(1, validationError.errors.size());
    assertEquals("name", validationError.errors.getFirst().field);
    assertEquals("required", validationError.errors.getFirst().message);
  }
}
