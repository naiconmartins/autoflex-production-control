package org.autoflex.adapters.inbound.resources;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.autoflex.adapters.inbound.dto.request.UserRequestDTO;
import org.autoflex.adapters.inbound.dto.response.UserResponseDTO;
import org.autoflex.adapters.inbound.mappers.UserMapper;
import org.autoflex.application.commands.InsertUserCommand;
import org.autoflex.application.usecases.UserUseCase;
import org.autoflex.domain.User;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

  @Inject UserUseCase userService;
  @Inject UserMapper userMapper;
  @Inject SecurityIdentity securityIdentity;

  @POST
  @RolesAllowed("ADMIN")
  public Response insert(@Valid UserRequestDTO dto) {
    InsertUserCommand user = userMapper.toInsertUserCommand(dto);
    User created = userService.insert(user);
    UserResponseDTO responseDTO = new UserResponseDTO(created);

    var location = UriBuilder.fromPath("/user/{id}").build(created.getId());

    return Response.created(location).entity(responseDTO).build();
  }

  @GET
  @Path("/me")
  @RolesAllowed({"USER", "ADMIN"})
  public UserResponseDTO getCurrentUser() {
    String email = securityIdentity.getPrincipal().getName();
    User user = userService.findByEmail(email);
    return new UserResponseDTO(user);
  }
}
