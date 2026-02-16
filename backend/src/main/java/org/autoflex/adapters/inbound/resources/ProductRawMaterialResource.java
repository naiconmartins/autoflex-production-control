package org.autoflex.adapters.inbound.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.autoflex.adapters.inbound.dto.request.ProductRawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.response.ProductRawMaterialResponseDTO;
import org.autoflex.adapters.inbound.mappers.ProductRawMaterialMapper;
import org.autoflex.application.commands.ProductRawMaterialCommand;
import org.autoflex.application.usecases.ProductRawMaterialUseCase;
import org.autoflex.domain.ProductRawMaterial;

@Path("/products/{productId}/raw-materials")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductRawMaterialResource {

  @Inject ProductRawMaterialUseCase service;
  @Inject ProductRawMaterialMapper mapper;

  @POST
  @RolesAllowed({"ADMIN", "USER"})
  public Response add(
      @PathParam("productId") Long productId, @Valid ProductRawMaterialRequestDTO dto) {
    ProductRawMaterialCommand cmd = mapper.toCommand(dto);
    ProductRawMaterial created = service.add(productId, cmd);
    ProductRawMaterialResponseDTO response = mapper.toResponse(created);
    return Response.status(Response.Status.CREATED).entity(response).build();
  }

  @GET
  @RolesAllowed({"ADMIN", "USER"})
  public Response list(@PathParam("productId") Long productId) {
    List<ProductRawMaterial> list = service.listByProduct(productId);
    List<ProductRawMaterialResponseDTO> response = mapper.toList(list);
    return Response.ok().entity(response).build();
  }

  @PUT
  @Path("/{rawMaterialId}")
  @RolesAllowed({"ADMIN", "USER"})
  public Response updateRequiredQuantity(
      @PathParam("productId") Long productId,
      @PathParam("rawMaterialId") Long rawMaterialId,
      @Valid ProductRawMaterialRequestDTO dto) {
    ProductRawMaterialCommand cmd = mapper.toCommand(dto);
    ProductRawMaterial updated = service.updateRequiredQuantity(productId, rawMaterialId, cmd);
    ProductRawMaterialResponseDTO response = mapper.toResponse(updated);
    return Response.ok(response).build();
  }

  @DELETE
  @Path("/{rawMaterialId}")
  @RolesAllowed({"ADMIN", "USER"})
  public Response remove(
      @PathParam("productId") Long productId, @PathParam("rawMaterialId") Long rawMaterialId) {
    service.remove(productId, rawMaterialId);
    return Response.noContent().build();
  }
}
