package org.autoflex.adapters.inbound.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import org.autoflex.fixtures.DtoFixture;
import org.junit.jupiter.api.Test;

class ProductRequestDTOTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void validate_shouldPass_whenValidRequest() {
    ProductRequestDTO dto = DtoFixture.createValidProductRequestDTO();
    assertTrue(validator.validate(dto).isEmpty());
  }

  @Test
  void validate_shouldFail_whenRawMaterialsIsEmpty() {
    ProductRequestDTO dto = DtoFixture.createValidProductRequestDTO();
    dto.rawMaterials = List.of();
    assertEquals(1, validator.validate(dto).size());
  }

  @Test
  void validate_shouldFail_whenNestedRawMaterialIsInvalid() {
    ProductRequestDTO dto = DtoFixture.createValidProductRequestDTO();
    dto.rawMaterials = List.of(new ProductRawMaterialRequestDTO(null, null));
    assertEquals(2, validator.validate(dto).size());
  }
}
