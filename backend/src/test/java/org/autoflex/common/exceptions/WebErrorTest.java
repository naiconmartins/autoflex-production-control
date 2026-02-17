package org.autoflex.common.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class WebErrorTest {

  @Test
  void constructor_shouldSetFields_whenAllArgsAreProvided() {
    Instant now = Instant.now();
    WebError webError = new WebError(now, 422, "Invalid data", "/products");

    assertEquals(now, webError.timestamp);
    assertEquals(422, webError.status);
    assertEquals("Invalid data", webError.error);
    assertEquals("/products", webError.path);
  }
}
