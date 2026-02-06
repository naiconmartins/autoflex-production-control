package org.autoflex.web.handlers;

import jakarta.validation.ConstraintViolation;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.autoflex.web.dto.ValidationErrorDTO;
import jakarta.validation.ConstraintViolationException;

import java.time.Instant;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException e) {
        int status = 422;
        String path = uriInfo.getAbsolutePath().getPath();
        ValidationErrorDTO err = new ValidationErrorDTO(Instant.now(), status, "Invalid data", path);

        e.getConstraintViolations().stream().findFirst().ifPresent(v -> {
            String fullPath = v.getPropertyPath().toString();
            String[] nodes = fullPath.split("\\.");
            String field = nodes[nodes.length - 1];

            err.addError(field, v.getMessage());
        });

        return Response.status(status).entity(err).build();
    }
}
