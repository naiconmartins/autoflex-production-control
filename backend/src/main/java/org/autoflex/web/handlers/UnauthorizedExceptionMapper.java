package org.autoflex.web.handlers;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.autoflex.web.dto.WebErrorDTO;
import org.autoflex.web.exceptions.UnauthorizedException;

import java.time.Instant;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(UnauthorizedException e) {
        int status = Response.Status.UNAUTHORIZED.getStatusCode();

        String message = (e.getMessage() == null || e.getMessage().isBlank())
                ? "Unauthorized"
                : e.getMessage();

        String path = uriInfo.getPath();
        if (!path.startsWith("/")) path = "/" + path;

        WebErrorDTO err = new WebErrorDTO(Instant.now(), status, message, path);
        return Response.status(status).entity(err).build();
    }
}
