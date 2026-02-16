package org.autoflex.adapters.inbound.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.autoflex.adapters.inbound.dto.request.PageRequestDTO;
import org.autoflex.adapters.inbound.dto.request.ProductRequestDTO;
import org.autoflex.adapters.inbound.dto.response.PageResponseDTO;
import org.autoflex.adapters.inbound.dto.response.ProductResponseDTO;
import org.autoflex.application.services.ProductService;

@Path("/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

  @Inject ProductService productService;

  @POST
  @RolesAllowed({"ADMIN", "USER"})
  public Response insert(@Valid ProductRequestDTO dto) {
    ProductResponseDTO created = productService.insert(dto);

    var location = UriBuilder.fromPath("/products/{id}").build(created.getId());

    return Response.created(location).entity(created).build();
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "USER"})
  public Response update(@PathParam("id") Long id, @Valid ProductRequestDTO dto) {
    ProductResponseDTO product = productService.update(id, dto);
    return Response.ok(product).build();
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed({"ADMIN"})
  public Response delete(@PathParam("id") Long id) {
    productService.delete(id);
    return Response.noContent().build();
  }

  @GET
  @RolesAllowed({"ADMIN", "USER"})
  public PageResponseDTO<ProductResponseDTO> findAll(@BeanParam PageRequestDTO dto) {
    return productService.findAll(dto);
  }

  @GET
  @Path("/search")
  @RolesAllowed({"ADMIN", "USER"})
  public PageResponseDTO<ProductResponseDTO> findByName(
      @QueryParam("name") String name, @BeanParam PageRequestDTO dto) {
    return productService.findByName(name, dto);
  }

  @GET
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "USER"})
  public ProductResponseDTO findById(@PathParam("id") Long id) {
    return productService.findById(id);
  }
}
