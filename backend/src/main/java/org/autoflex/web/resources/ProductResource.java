package org.autoflex.web.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.autoflex.web.dto.PageRequestDTO;
import org.autoflex.web.dto.PageResponseDTO;
import org.autoflex.web.dto.ProductRequestDTO;
import org.autoflex.application.services.ProductService;
import org.autoflex.web.dto.ProductResponseDTO;

@Path("/product")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService productService;

    @POST
    @RolesAllowed({"ADMIN", "USER"})
    public Response insert(@Valid ProductRequestDTO dto) {
        ProductResponseDTO created = productService.insert(dto);

        var location = UriBuilder.fromPath("/products/{id}")
                .build(created.getId());

        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "USER"})
    public Response update(@PathParam("id") Long id, @Valid ProductRequestDTO dto) {
        ProductResponseDTO product = productService.update(id, dto);
        return Response.ok().entity(product).build();
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
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "USER"})
    public ProductResponseDTO findAll(@PathParam("id") Long id) {
        return productService.findById(id);
    }
}
