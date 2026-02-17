package org.autoflex.common.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.WebError;
import org.junit.jupiter.api.Test;

class ConflictExceptionMapperTest {

  @Test
  void toResponse_shouldReturn409_whenConflictExceptionIsThrown() {
    ConflictExceptionMapper mapper = new ConflictExceptionMapper();
    UriInfo uriInfo = mock(UriInfo.class);
    when(uriInfo.getPath()).thenReturn("/products");
    mapper.uriInfo = uriInfo;

    Response response = mapper.toResponse(new ConflictException("conflict"));

    assertEquals(409, response.getStatus());
    WebError err = (WebError) response.getEntity();
    assertEquals("conflict", err.error);
    assertEquals("/products", err.path);
    assertNotNull(err.timestamp);
  }
}
