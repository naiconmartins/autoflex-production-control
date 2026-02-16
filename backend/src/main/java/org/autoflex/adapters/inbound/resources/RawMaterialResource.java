package org.autoflex.adapters.inbound.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.autoflex.adapters.inbound.dto.request.PageRequestDTO;
import org.autoflex.adapters.inbound.dto.request.RawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.response.PageResponseDTO;
import org.autoflex.adapters.inbound.dto.response.RawMaterialResponseDTO;
import org.autoflex.adapters.inbound.mappers.RawMaterialMapper;
import org.autoflex.adapters.inbound.mappers.SearchMapper;
import org.autoflex.application.commands.RawMaterialCommand;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.application.dto.SearchQuery;
import org.autoflex.application.usecases.RawMaterialUseCase;
import org.autoflex.domain.RawMaterial;

@Path("/raw-materials")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RawMaterialResource {

  @Inject RawMaterialUseCase rawMaterialUseCase;
  @Inject RawMaterialMapper mapper;
  @Inject SearchMapper searchMapper;

  @POST
  @RolesAllowed({"ADMIN", "USER"})
  public Response insert(@Valid RawMaterialRequestDTO dto) {
    RawMaterialCommand cmd = mapper.toCommand(dto);
    RawMaterial created = rawMaterialUseCase.insert(cmd);
    RawMaterialResponseDTO response = new RawMaterialResponseDTO(created);

    var location = UriBuilder.fromPath("/raw-materials/{id}").build(created.getId());

    return Response.created(location).entity(response).build();
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "USER"})
  public Response update(@PathParam("id") Long id, @Valid RawMaterialRequestDTO dto) {
    RawMaterialCommand cmd = mapper.toCommand(dto);
    RawMaterial updated = rawMaterialUseCase.update(id, cmd);
    RawMaterialResponseDTO response = new RawMaterialResponseDTO(updated);
    return Response.ok().entity(response).build();
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed({"ADMIN"})
  public Response delete(@PathParam("id") Long id) {
    rawMaterialUseCase.delete(id);
    return Response.noContent().build();
  }

  @GET
  @RolesAllowed({"ADMIN", "USER"})
  public PageResponseDTO<RawMaterialResponseDTO> findAll(@BeanParam PageRequestDTO dto) {
    SearchQuery query = searchMapper.toQuery(dto);
    PagedModel<RawMaterial> pagedModel = rawMaterialUseCase.findAll(query);
    return mapper.toResponse(pagedModel, dto.page, dto.size);
  }

  @GET
  @Path("/search")
  @RolesAllowed({"ADMIN", "USER"})
  public PageResponseDTO<RawMaterialResponseDTO> findByName(
      @QueryParam("name") String name, @BeanParam PageRequestDTO dto) {
    SearchQuery query = searchMapper.toQuery(dto);
    PagedModel<RawMaterial> pagedModel = rawMaterialUseCase.findByName(name, query);
    return mapper.toResponse(pagedModel, dto.page, dto.size);
  }

  @GET
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "USER"})
  public Response findById(@PathParam("id") Long id) {
    RawMaterial rm = rawMaterialUseCase.findById(id);
    RawMaterialResponseDTO response = new RawMaterialResponseDTO(rm);
    return Response.ok(response).build();
  }
}
