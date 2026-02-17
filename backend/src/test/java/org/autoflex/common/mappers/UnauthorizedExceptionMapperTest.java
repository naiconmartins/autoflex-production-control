package org.autoflex.common.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.autoflex.common.exceptions.UnauthorizedException;
import org.autoflex.common.exceptions.WebError;
import org.junit.jupiter.api.Test;

class UnauthorizedExceptionMapperTest {

  @Test
  void toResponse_shouldReturn401WithMessage_whenMessageIsProvided() {
    UnauthorizedExceptionMapper mapper = new UnauthorizedExceptionMapper();
    UriInfo uriInfo = mock(UriInfo.class);
    when(uriInfo.getPath()).thenReturn("/auth/login");
    mapper.uriInfo = uriInfo;

    Response response = mapper.toResponse(new UnauthorizedException("invalid credentials"));

    assertEquals(401, response.getStatus());
    WebError err = (WebError) response.getEntity();
    assertEquals("invalid credentials", err.error);
    assertEquals("/auth/login", err.path);
    assertNotNull(err.timestamp);
  }

  @Test
  void toResponse_shouldReturn401WithDefaultMessage_whenMessageIsBlank() {
    UnauthorizedExceptionMapper mapper = new UnauthorizedExceptionMapper();
    UriInfo uriInfo = mock(UriInfo.class);
    when(uriInfo.getPath()).thenReturn("/auth/login");
    mapper.uriInfo = uriInfo;

    Response response = mapper.toResponse(new UnauthorizedException(" "));

    assertEquals(401, response.getStatus());
    WebError err = (WebError) response.getEntity();
    assertEquals("Unauthorized", err.error);
    assertEquals("/auth/login", err.path);
    assertNotNull(err.timestamp);
  }
}
