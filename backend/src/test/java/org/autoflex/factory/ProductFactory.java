package org.autoflex.factory;

import org.autoflex.domain.entities.Product;
import org.autoflex.web.dto.ProductRawMaterialRequestDTO;
import org.autoflex.web.dto.ProductRequestDTO;

import java.math.BigDecimal;
import java.util.List;

public class ProductFactory {
    public static ProductRequestDTO createProductRequestDTO() {
        return new ProductRequestDTO(
                "PROD001",
                "Product Test",
                new BigDecimal("100.00"),
                List.of()
        );
    }

    public static ProductRequestDTO createProductRequestDTOWithRawMaterials() {
        return new ProductRequestDTO(
                "PROD001",
                "Product Test",
                new BigDecimal("100.00"),
                List.of(
                        new ProductRawMaterialRequestDTO(1L, new BigDecimal("10.00")),
                        new ProductRawMaterialRequestDTO(2L, new BigDecimal("5.00"))
                )
        );
    }

    public static Product createProductWithCode(String code) {
        return new Product(code, "Test Product", new BigDecimal("100.00"));
    }
}
