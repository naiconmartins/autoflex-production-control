package org.autoflex.application.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.autoflex.application.commands.RawMaterialCommand;
import org.autoflex.common.exceptions.InvalidDataException;
import org.junit.jupiter.api.Test;

public class RawMaterialCommandTest {

  private static final String VALID_CODE = "RAW001";
  private static final String VALID_NAME = "MDF Board";
  private static final BigDecimal VALID_STOCK_QUANTITY = new BigDecimal("1000.00");

  @Test
  void shouldThrowInvalidDataException_whenCodeIsNull() {
    assertInvalidCommand(
        null, VALID_NAME, VALID_STOCK_QUANTITY, "Raw material code is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenCodeIsEmpty() {
    assertInvalidCommand("", VALID_NAME, VALID_STOCK_QUANTITY, "Raw material code is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenNameIsNull() {
    assertInvalidCommand(VALID_CODE, null, VALID_STOCK_QUANTITY, "Raw material name is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenNameIsEmpty() {
    assertInvalidCommand(VALID_CODE, "", VALID_STOCK_QUANTITY, "Raw material name is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenStockQuantityIsNull() {
    assertInvalidCommand(VALID_CODE, VALID_NAME, null, "Stock quantity is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenStockQuantityIsNegative() {
    assertInvalidCommand(
        VALID_CODE,
        VALID_NAME,
        new BigDecimal("-1.00"),
        "Stock quantity cannot be negative");
  }

  @Test
  void shouldCreateCommand_whenStockQuantityIsZero() {
    assertDoesNotThrow(() -> createCommand(VALID_CODE, VALID_NAME, BigDecimal.ZERO));
  }

  @Test
  void shouldCreateCommand_whenValidData() {
    assertDoesNotThrow(() -> createCommand(VALID_CODE, VALID_NAME, VALID_STOCK_QUANTITY));
  }

  private static void assertInvalidCommand(
      String code, String name, BigDecimal stockQuantity, String expectedMessage) {
    InvalidDataException ex =
        assertThrows(
            InvalidDataException.class, () -> createCommand(code, name, stockQuantity));

    assertEquals(expectedMessage, ex.getMessage());
  }

  private static RawMaterialCommand createCommand(
      String code, String name, BigDecimal stockQuantity) {
    return new RawMaterialCommand(code, name, stockQuantity);
  }
}
