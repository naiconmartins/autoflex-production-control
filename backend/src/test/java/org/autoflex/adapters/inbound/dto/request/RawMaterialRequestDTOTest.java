package org.autoflex.adapters.inbound.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import org.autoflex.fixtures.DtoFixture;
import org.junit.jupiter.api.Test;

class RawMaterialRequestDTOTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void validate_shouldPass_whenValidRequest() {
    RawMaterialRequestDTO dto = DtoFixture.createValidRawMaterialRequestDTO();
    assertTrue(validator.validate(dto).isEmpty());
  }

  @Test
  void validate_shouldFail_whenStockQuantityIsNegative() {
    RawMaterialRequestDTO dto = DtoFixture.createValidRawMaterialRequestDTO();
    dto.stockQuantity = new BigDecimal("-1.00");
    assertEquals(1, validator.validate(dto).size());
  }
}
