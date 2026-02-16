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
import org.autoflex.adapters.inbound.mappers.ProductMapper;
import org.autoflex.adapters.inbound.mappers.SearchMapper;
import org.autoflex.application.commands.ProductCommand;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.application.dto.SearchQuery;
import org.autoflex.application.usecases.ProductUseCase;
import org.autoflex.domain.Product;

@Path("/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

  @Inject ProductUseCase productUseCase;
  @Inject ProductMapper mapper;
  @Inject SearchMapper searchMapper;

  @POST
  @RolesAllowed({"ADMIN", "USER"})
  public Response insert(@Valid ProductRequestDTO dto) {
    ProductCommand cmd = mapper.toInsertCommand(dto);
    Product created = productUseCase.insert(cmd);
    ProductResponseDTO response = new ProductResponseDTO(created);

    var location = UriBuilder.fromPath("/products/{id}").build(created.getId());

    return Response.created(location).entity(response).build();
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "USER"})
  public Response update(@PathParam("id") Long id, @Valid ProductRequestDTO dto) {
    ProductCommand cmd = mapper.toInsertCommand(dto);
    Product product = productUseCase.update(id, cmd);
    ProductResponseDTO response = new ProductResponseDTO(product);
    return Response.ok(response).build();
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed({"ADMIN"})
  public Response delete(@PathParam("id") Long id) {
    productUseCase.delete(id);
    return Response.noContent().build();
  }

  @GET
  @RolesAllowed({"ADMIN", "USER"})
  public PageResponseDTO<ProductResponseDTO> findAll(@BeanParam PageRequestDTO dto) {
    SearchQuery query = searchMapper.toQuery(dto);
    PagedModel<Product> pagedModel = productUseCase.findAll(query);
    return mapper.toResponse(pagedModel, dto.page, dto.size);
  }

  @GET
  @Path("/search")
  @RolesAllowed({"ADMIN", "USER"})
  public PageResponseDTO<ProductResponseDTO> findByName(
      @QueryParam("name") String name, @BeanParam PageRequestDTO dto) {
    SearchQuery query = searchMapper.toQuery(dto);
    PagedModel<Product> pagedModel = productUseCase.findByName(name, query);

    return mapper.toResponse(pagedModel, dto.page, dto.size);
  }

  @GET
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "USER"})
  public Response findById(@PathParam("id") Long id) {
    Product product = productUseCase.findById(id);
    ProductResponseDTO response = new ProductResponseDTO(product);

    return Response.ok(response).build();
  }
}
