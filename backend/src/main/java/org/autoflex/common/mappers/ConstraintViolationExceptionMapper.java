package org.autoflex.common.mappers;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;
import org.autoflex.common.exceptions.ValidationError;

@Provider
public class ConstraintViolationExceptionMapper
    implements ExceptionMapper<ConstraintViolationException> {

  @Context UriInfo uriInfo;

  @Override
  public Response toResponse(ConstraintViolationException e) {
    int status = 422;
    String path = uriInfo.getAbsolutePath().getPath();
    ValidationError err = new ValidationError(Instant.now(), status, "Invalid data", path);

    e.getConstraintViolations().stream()
        .findFirst()
        .ifPresent(
            v -> {
              String fullPath = v.getPropertyPath().toString();
              String[] nodes = fullPath.split("\\.");
              String field = nodes[nodes.length - 1];

              err.addError(field, v.getMessage());
            });

    return Response.status(status).entity(err).build();
  }
}
