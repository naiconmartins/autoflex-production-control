package org.autoflex.common.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.autoflex.common.exceptions.DatabaseException;
import org.autoflex.common.exceptions.WebError;
import org.junit.jupiter.api.Test;

class DatabaseExceptionMapperTest {

  @Test
  void toResponse_shouldReturn400_whenDatabaseExceptionIsThrown() {
    DatabaseExceptionMapper mapper = new DatabaseExceptionMapper();
    UriInfo uriInfo = mock(UriInfo.class);
    when(uriInfo.getPath()).thenReturn("/products");
    mapper.uriInfo = uriInfo;

    Response response = mapper.toResponse(new DatabaseException("db error"));

    assertEquals(400, response.getStatus());
    WebError err = (WebError) response.getEntity();
    assertEquals("db error", err.error);
    assertEquals("/products", err.path);
    assertNotNull(err.timestamp);
  }
}
