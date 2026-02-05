package org.autoflex.web.handlers;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.autoflex.web.dto.WebErrorDTO;
import org.autoflex.web.exceptions.ResourceNotFoundException;

import java.time.Instant;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ResourceNotFoundException e) {
        int status = Response.Status.NOT_FOUND.getStatusCode();
        WebErrorDTO err = new WebErrorDTO(Instant.now(), status, e.getMessage(), "/" + uriInfo.getPath());
        return Response.status(status).entity(err).build();
    }
}
