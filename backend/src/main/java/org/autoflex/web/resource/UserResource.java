package org.autoflex.web.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.autoflex.application.services.UserService;
import org.autoflex.web.dto.UserRequestDTO;
import org.autoflex.web.dto.UserResponseDTO;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @POST
    @RolesAllowed("ADMIN")
    public Response insert(@Valid UserRequestDTO dto) {
        UserResponseDTO created = userService.insert(dto);

        var location = UriBuilder.fromPath("/user/{id}")
                .build(created.getId());

        return Response.created(location).entity(created).build();
    }
}
