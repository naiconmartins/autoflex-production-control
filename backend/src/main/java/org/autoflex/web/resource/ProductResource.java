package org.autoflex.web.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.autoflex.web.dto.ProductRequestDTO;
import org.autoflex.domain.entities.Product;
import org.autoflex.application.services.ProductService;

@Path("/product")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService productService;

    @POST
    public Response insert(@Valid ProductRequestDTO dto) {
        Product created = productService.insert(dto);

        var location = UriBuilder.fromPath("/products/{id}")
                .build(created.getId());

        return Response.created(location).entity(created).build();
    }
}
