package org.autoflex.adapters.inbound.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.autoflex.fixtures.DtoFixture;
import org.junit.jupiter.api.Test;

class LoginRequestDTOTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void validate_shouldPass_whenValidRequest() {
    LoginRequestDTO dto = DtoFixture.createValidLoginRequestDTO();
    assertTrue(validator.validate(dto).isEmpty());
  }

  @Test
  void validate_shouldFail_whenEmailIsInvalid() {
    LoginRequestDTO dto = DtoFixture.createValidLoginRequestDTO();
    dto.setEmail("invalid");
    assertEquals(1, validator.validate(dto).size());
  }
}
