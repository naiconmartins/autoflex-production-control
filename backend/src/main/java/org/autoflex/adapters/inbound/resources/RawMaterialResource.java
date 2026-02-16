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
import org.autoflex.application.services.RawMaterialService;

@Path("/raw-materials")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RawMaterialResource {
  @Inject RawMaterialService rawMaterialService;

  @POST
  @RolesAllowed({"ADMIN", "USER"})
  public Response insert(@Valid RawMaterialRequestDTO dto) {
    RawMaterialResponseDTO created = rawMaterialService.insert(dto);

    var location = UriBuilder.fromPath("/raw-materials/{id}").build(created.getId());

    return Response.created(location).entity(created).build();
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "USER"})
  public Response update(@PathParam("id") Long id, @Valid RawMaterialRequestDTO dto) {
    RawMaterialResponseDTO rawMaterial = rawMaterialService.update(id, dto);
    return Response.ok().entity(rawMaterial).build();
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed({"ADMIN"})
  public Response delete(@PathParam("id") Long id) {
    rawMaterialService.delete(id);
    return Response.noContent().build();
  }

  @GET
  @RolesAllowed({"ADMIN", "USER"})
  public PageResponseDTO<RawMaterialResponseDTO> findAll(@BeanParam PageRequestDTO dto) {
    return rawMaterialService.findAll(dto);
  }

  @GET
  @Path("/search")
  @RolesAllowed({"ADMIN", "USER"})
  public PageResponseDTO<RawMaterialResponseDTO> findByName(
      @QueryParam("name") String name, @BeanParam PageRequestDTO dto) {
    return rawMaterialService.findByName(name, dto);
  }

  @GET
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "USER"})
  public RawMaterialResponseDTO findAll(@PathParam("id") Long id) {
    return rawMaterialService.findById(id);
  }
}
