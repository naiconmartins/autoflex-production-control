package org.autoflex.adapters.inbound.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import org.autoflex.fixtures.DtoFixture;
import org.junit.jupiter.api.Test;

class ProductRawMaterialRequestDTOTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void validate_shouldPass_whenValidRequest() {
    ProductRawMaterialRequestDTO dto = DtoFixture.createValidProductRawMaterialRequestDTO();
    assertTrue(validator.validate(dto).isEmpty());
  }

  @Test
  void validate_shouldFail_whenRequiredQuantityIsZero() {
    ProductRawMaterialRequestDTO dto = DtoFixture.createValidProductRawMaterialRequestDTO();
    dto.requiredQuantity = BigDecimal.ZERO;
    assertEquals(1, validator.validate(dto).size());
  }
}
