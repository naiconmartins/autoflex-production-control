package org.autoflex.application.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.autoflex.application.commands.ProductCommand;
import org.autoflex.common.exceptions.InvalidDataException;
import org.autoflex.fixtures.RawMaterialFixture;
import org.junit.jupiter.api.Test;

public class ProductCommandTest {

  private static final String VALID_CODE = "PROD001";
  private static final String VALID_NAME = "Dining Table Oak 2.00m";
  private static final BigDecimal VALID_PRICE = new BigDecimal("1850.00");

  @Test
  void shouldThrowInvalidDataException_whenCodeIsNull() {
    assertInvalidCommand(
        null, VALID_NAME, VALID_PRICE, validRawMaterials(), "Product code is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenCodeIsEmpty() {
    assertInvalidCommand(
        "", VALID_NAME, VALID_PRICE, validRawMaterials(), "Product code is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenNameIsNull() {
    assertInvalidCommand(
        VALID_CODE, null, VALID_PRICE, validRawMaterials(), "Product name is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenNameIsEmpty() {
    assertInvalidCommand(
        VALID_CODE, "", VALID_PRICE, validRawMaterials(), "Product name is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenPriceIsNull() {
    assertInvalidCommand(
        VALID_CODE, VALID_NAME, null, validRawMaterials(), "Product price is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenPriceIsZero() {
    assertInvalidCommand(
        VALID_CODE,
        VALID_NAME,
        BigDecimal.ZERO,
        validRawMaterials(),
        "Price must be greater than zero");
  }

  @Test
  void shouldThrowInvalidDataException_whenPriceIsNegative() {
    assertInvalidCommand(
        VALID_CODE,
        VALID_NAME,
        new BigDecimal("-1"),
        validRawMaterials(),
        "Price must be greater than zero");
  }

  @Test
  void shouldCreateCommand_whenRawMaterialsIsNull() {
    assertDoesNotThrow(() -> createCommand(VALID_CODE, VALID_NAME, VALID_PRICE, null));
  }

  @Test
  void shouldCreateCommand_whenRawMaterialsIsEmpty() {
    assertDoesNotThrow(() -> createCommand(VALID_CODE, VALID_NAME, VALID_PRICE, List.of()));
  }

  @Test
  void shouldThrowInvalidDataException_whenRawMaterialIdIsNull() {
    assertInvalidCommand(
        VALID_CODE,
        VALID_NAME,
        VALID_PRICE,
        List.of(new ProductCommand.RawMaterialItem(null, new BigDecimal("150.00"))),
        "Raw material id is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenRawMaterialRequiredQuantityIsNull() {
    assertInvalidCommand(
        VALID_CODE,
        VALID_NAME,
        VALID_PRICE,
        List.of(new ProductCommand.RawMaterialItem(1L, null)),
        "Required quantity is required");
  }

  @Test
  void shouldThrowInvalidDataException_whenRawMaterialRequiredQuantityIsZero() {
    assertInvalidCommand(
        VALID_CODE,
        VALID_NAME,
        VALID_PRICE,
        List.of(new ProductCommand.RawMaterialItem(1L, BigDecimal.ZERO)),
        "Required quantity must be greater than zero");
  }

  @Test
  void shouldThrowInvalidDataException_whenRawMaterialRequiredQuantityIsNegative() {
    assertInvalidCommand(
        VALID_CODE,
        VALID_NAME,
        VALID_PRICE,
        List.of(new ProductCommand.RawMaterialItem(1L, new BigDecimal("-1"))),
        "Required quantity must be greater than zero");
  }

  @Test
  void shouldThrowInvalidDataException_whenRawMaterialItemIsNull() {
    assertInvalidCommand(
        VALID_CODE,
        VALID_NAME,
        VALID_PRICE,
        Collections.singletonList(null),
        "Raw material item is required");
  }

  private static void assertInvalidCommand(
      String code,
      String name,
      BigDecimal price,
      List<ProductCommand.RawMaterialItem> rawMaterials,
      String expectedMessage) {
    InvalidDataException ex =
        assertThrows(
            InvalidDataException.class, () -> createCommand(code, name, price, rawMaterials));

    assertEquals(expectedMessage, ex.getMessage());
  }

  private static ProductCommand createCommand(
      String code,
      String name,
      BigDecimal price,
      List<ProductCommand.RawMaterialItem> rawMaterials) {
    return new ProductCommand(code, name, price, rawMaterials);
  }

  private static List<ProductCommand.RawMaterialItem> validRawMaterials() {
    return List.of(RawMaterialFixture.createListRawMaterialItem());
  }
}
