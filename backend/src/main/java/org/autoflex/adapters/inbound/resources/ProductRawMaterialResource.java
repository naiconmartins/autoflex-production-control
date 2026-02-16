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
import org.autoflex.application.services.ProductRawMaterialService;

@Path("/products/{productId}/raw-materials")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductRawMaterialResource {

  @Inject ProductRawMaterialService service;

  @POST
  @RolesAllowed({"ADMIN", "USER"})
  public Response add(
      @PathParam("productId") Long productId, @Valid ProductRawMaterialRequestDTO dto) {
    ProductRawMaterialResponseDTO created = service.add(productId, dto);
    return Response.status(Response.Status.CREATED).entity(created).build();
  }

  @GET
  @RolesAllowed({"ADMIN", "USER"})
  public List<ProductRawMaterialResponseDTO> list(@PathParam("productId") Long productId) {
    return service.listByProduct(productId);
  }

  @PUT
  @Path("/{rawMaterialId}")
  @RolesAllowed({"ADMIN", "USER"})
  public Response updateRequiredQuantity(
      @PathParam("productId") Long productId,
      @PathParam("rawMaterialId") Long rawMaterialId,
      @Valid ProductRawMaterialRequestDTO dto) {
    ProductRawMaterialResponseDTO updated =
        service.updateRequiredQuantity(productId, rawMaterialId, dto);
    return Response.ok(updated).build();
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
