package org.autoflex.web.handlers;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.autoflex.web.dto.WebErrorDTO;
import org.autoflex.web.exceptions.ForbiddenException;

import java.time.Instant;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {
    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ForbiddenException e) {
        int status = Response.Status.FORBIDDEN.getStatusCode();
        WebErrorDTO err = new WebErrorDTO(Instant.now(), status, e.getMessage(), "/" + uriInfo.getPath());
        return Response.status(status).entity(err).build();
    }
}
