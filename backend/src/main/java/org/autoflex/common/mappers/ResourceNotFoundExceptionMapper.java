package org.autoflex.common.mappers;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.common.exceptions.WebError;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

  @Context UriInfo uriInfo;

  @Override
  public Response toResponse(ResourceNotFoundException e) {
    int status = Response.Status.NOT_FOUND.getStatusCode();
    WebError err = new WebError(Instant.now(), status, e.getMessage(), uriInfo.getPath());
    return Response.status(status).entity(err).build();
  }
}
