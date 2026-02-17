package org.autoflex.adapters.inbound.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.autoflex.fixtures.DtoFixture;
import org.junit.jupiter.api.Test;

class UserRequestDTOTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void validate_shouldPass_whenValidRequest() {
    UserRequestDTO dto = DtoFixture.createValidUserRequestDTO();
    assertTrue(validator.validate(dto).isEmpty());
  }

  @Test
  void validate_shouldFail_whenEmailIsNull() {
    UserRequestDTO dto = DtoFixture.createValidUserRequestDTO();
    dto.email = null;
    assertEquals(2, validator.validate(dto).size());
  }

  @Test
  void validate_shouldFail_whenRoleIsBlank() {
    UserRequestDTO dto = DtoFixture.createValidUserRequestDTO();
    dto.role = "";
    assertEquals(1, validator.validate(dto).size());
  }
}
