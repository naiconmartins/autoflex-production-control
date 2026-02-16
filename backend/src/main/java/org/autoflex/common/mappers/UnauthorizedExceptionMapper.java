package org.autoflex.common.mappers;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;
import org.autoflex.common.exceptions.UnauthorizedException;
import org.autoflex.common.exceptions.WebError;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {

  @Context UriInfo uriInfo;

  @Override
  public Response toResponse(UnauthorizedException e) {
    int status = Response.Status.UNAUTHORIZED.getStatusCode();

    String message =
        (e.getMessage() == null || e.getMessage().isBlank()) ? "Unauthorized" : e.getMessage();

    WebError err = new WebError(Instant.now(), status, message, uriInfo.getPath());

    return Response.status(status).entity(err).build();
  }
}
