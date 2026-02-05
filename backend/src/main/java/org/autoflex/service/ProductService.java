package org.autoflex.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.autoflex.dto.ProductRequestDTO;
import org.autoflex.entity.Product;

@ApplicationScoped
public class ProductService {

    @Transactional
    public Product insert(ProductRequestDTO dto) {
        if (Product.find("code", dto.code).firstResult() != null) {
            throw new WebApplicationException(
                    "Product code already exists",
                    Response.Status.CONFLICT
            );
        }

        Product product = new Product(
                dto.code,
                dto.name,
                dto.price
        );

        product.persist();
        return product;
    }
}
