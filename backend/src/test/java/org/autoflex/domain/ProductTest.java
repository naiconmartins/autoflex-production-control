package org.autoflex.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.autoflex.common.exceptions.InvalidDataException;
import org.junit.jupiter.api.Test;

class ProductTest {

  @Test
  void constructor_shouldCreateProduct_whenPriceIsValid() {
    Product product = new Product("PROD-001", "Table", new BigDecimal("10.00"));

    assertEquals("PROD-001", product.getCode());
    assertEquals("Table", product.getName());
    assertEquals(0, new BigDecimal("10.00").compareTo(product.getPrice()));
  }

  @Test
  void constructor_shouldThrowInvalidDataException_whenPriceIsNull() {
    InvalidDataException ex =
        assertThrows(InvalidDataException.class, () -> new Product("PROD-001", "Table", null));

    assertEquals("Product price is required", ex.getMessage());
  }

  @Test
  void constructor_shouldThrowInvalidDataException_whenPriceIsZero() {
    InvalidDataException ex =
        assertThrows(
            InvalidDataException.class,
            () -> new Product("PROD-001", "Table", BigDecimal.ZERO));

    assertEquals("Price must be greater than zero", ex.getMessage());
  }

  @Test
  void setPrice_shouldThrowInvalidDataException_whenPriceIsNegative() {
    Product product = new Product("PROD-001", "Table", new BigDecimal("10.00"));

    InvalidDataException ex =
        assertThrows(InvalidDataException.class, () -> product.setPrice(new BigDecimal("-1.00")));

    assertEquals("Price must be greater than zero", ex.getMessage());
  }

  @Test
  void updateData_shouldThrowInvalidDataException_whenPriceIsInvalid() {
    Product product = new Product("PROD-001", "Table", new BigDecimal("10.00"));

    InvalidDataException ex =
        assertThrows(
            InvalidDataException.class,
            () -> product.updateData("PROD-002", "Chair", BigDecimal.ZERO));

    assertEquals("Price must be greater than zero", ex.getMessage());
  }

  @Test
  void updateData_shouldUpdateFields_whenPriceIsValid() {
    Product product = new Product("PROD-001", "Table", new BigDecimal("10.00"));

    product.updateData("PROD-002", "Chair", new BigDecimal("20.00"));

    assertEquals("PROD-002", product.getCode());
    assertEquals("Chair", product.getName());
    assertEquals(0, new BigDecimal("20.00").compareTo(product.getPrice()));
  }
}
