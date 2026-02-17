package org.autoflex.common.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.common.exceptions.WebError;
import org.junit.jupiter.api.Test;

class ResourceNotFoundExceptionMapperTest {

  @Test
  void toResponse_shouldReturn404_whenResourceNotFoundExceptionIsThrown() {
    ResourceNotFoundExceptionMapper mapper = new ResourceNotFoundExceptionMapper();
    UriInfo uriInfo = mock(UriInfo.class);
    when(uriInfo.getPath()).thenReturn("/products/1");
    mapper.uriInfo = uriInfo;

    Response response = mapper.toResponse(new ResourceNotFoundException("not found"));

    assertEquals(404, response.getStatus());
    WebError err = (WebError) response.getEntity();
    assertEquals("not found", err.error);
    assertEquals("/products/1", err.path);
    assertNotNull(err.timestamp);
  }
}
