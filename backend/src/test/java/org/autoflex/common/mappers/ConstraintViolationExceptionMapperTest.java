package org.autoflex.common.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Set;
import org.autoflex.adapters.inbound.dto.request.RawMaterialRequestDTO;
import org.autoflex.common.exceptions.ValidationError;
import org.autoflex.fixtures.DtoFixture;
import org.junit.jupiter.api.Test;

class ConstraintViolationExceptionMapperTest {

  @Test
  void toResponse_shouldReturn422WithValidationError_whenConstraintViolationOccurs() {
    ConstraintViolationExceptionMapper mapper = new ConstraintViolationExceptionMapper();
    UriInfo uriInfo = mock(UriInfo.class);
    when(uriInfo.getAbsolutePath()).thenReturn(URI.create("http://localhost/raw-materials"));
    mapper.uriInfo = uriInfo;

    RawMaterialRequestDTO dto = DtoFixture.createValidRawMaterialRequestDTO();
    dto.code = null;

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Set<ConstraintViolation<RawMaterialRequestDTO>> violations = validator.validate(dto);
    ConstraintViolationException exception = new ConstraintViolationException(violations);

    Response response = mapper.toResponse(exception);

    assertEquals(422, response.getStatus());
    ValidationError err = (ValidationError) response.getEntity();
    assertNotNull(err.timestamp);
    assertEquals(422, err.status);
    assertEquals("Invalid data", err.error);
    assertEquals("/raw-materials", err.path);
    assertEquals(1, err.errors.size());
    assertEquals("code", err.errors.getFirst().field);
    assertTrue(err.errors.getFirst().message.contains("required"));
  }
}
