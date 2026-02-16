package org.autoflex.adapters.inbound.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.autoflex.adapters.inbound.dto.request.LoginRequestDTO;
import org.autoflex.adapters.inbound.mappers.LoginMapper;
import org.autoflex.application.commands.LoginCommand;
import org.autoflex.application.dto.TokenData;
import org.autoflex.application.usecases.AuthUseCase;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

  @Inject AuthUseCase authUseCase;
  @Inject LoginMapper mapper;

  @POST
  @Path("/login")
  @PermitAll
  public Response login(@Valid LoginRequestDTO dto) {
    LoginCommand cmd = mapper.toCommand(dto);
    TokenData tokenData = authUseCase.authenticate(cmd);

    return Response.ok(mapper.toResponse(tokenData)).build();
  }
}
