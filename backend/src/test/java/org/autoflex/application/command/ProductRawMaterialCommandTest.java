package org.autoflex.application.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.autoflex.application.commands.ProductRawMaterialCommand;
import org.autoflex.common.exceptions.InvalidDataException;
import org.junit.jupiter.api.Test;

public class ProductRawMaterialCommandTest {

  private static final Long VALID_RAW_MATERIAL_ID = 1L;
  private static final BigDecimal VALID_REQUIRED_QUANTITY = new BigDecimal("10.00");

  @Test
  void shouldThrowInvalidDataException_whenRawMaterialIdIsNull() {
    assertInvalidCommand(null, VALID_REQUIRED_QUANTITY, "Raw material id is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenRequiredQuantityIsNull() {
    assertInvalidCommand(VALID_RAW_MATERIAL_ID, null, "Required quantity is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenRequiredQuantityIsZero() {
    assertInvalidCommand(
        VALID_RAW_MATERIAL_ID, BigDecimal.ZERO, "Required quantity must be greater than zero");
  }

  @Test
  void shouldThrowInvalidDataException_whenRequiredQuantityIsNegative() {
    assertInvalidCommand(
        VALID_RAW_MATERIAL_ID,
        new BigDecimal("-1.00"),
        "Required quantity must be greater than zero");
  }

  @Test
  void shouldCreateCommand_whenRequiredQuantityIsPositive() {
    assertDoesNotThrow(() -> createCommand(VALID_RAW_MATERIAL_ID, VALID_REQUIRED_QUANTITY));
  }

  private static void assertInvalidCommand(
      Long rawMaterialId, BigDecimal requiredQuantity, String expectedMessage) {
    InvalidDataException ex =
        assertThrows(
            InvalidDataException.class, () -> createCommand(rawMaterialId, requiredQuantity));

    assertEquals(expectedMessage, ex.getMessage());
  }

  private static ProductRawMaterialCommand createCommand(
      Long rawMaterialId, BigDecimal requiredQuantity) {
    return new ProductRawMaterialCommand(rawMaterialId, requiredQuantity);
  }
}
