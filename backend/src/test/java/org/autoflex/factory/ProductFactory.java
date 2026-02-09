package org.autoflex.factory;

import org.autoflex.domain.entities.Product;
import org.autoflex.web.dto.ProductRawMaterialRequestDTO;
import org.autoflex.web.dto.ProductRawMaterialResponseDTO;
import org.autoflex.web.dto.ProductRequestDTO;
import org.autoflex.web.dto.ProductResponseDTO;

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
                "PROD-040",
                "Product Test",
                new BigDecimal("100.00"),
                List.of(
                        new ProductRawMaterialRequestDTO(1L, new BigDecimal("10.00")),
                        new ProductRawMaterialRequestDTO(2L, new BigDecimal("5.00"))
                )
        );
    }

    public static ProductResponseDTO createProductResponseDTOWithRawMaterials() {
        return new ProductResponseDTO(
                1L,
                "PROD001",
                "Product Test",
                new BigDecimal("100.00"),
                List.of(
                        new ProductRawMaterialResponseDTO(1L, "RAW001", "Raw Material 1", new BigDecimal("10.00")),
                        new ProductRawMaterialResponseDTO(2L, "RAW002", "Raw Material 2", new BigDecimal("5.00"))
                )
        );
    }

    public static Product createProductWithCode(String code) {
        return new Product(code, "Test Product", new BigDecimal("100.00"));
    }

    public static Product createProduct() {
        Product product = new Product("PROD001", "Product Test", new BigDecimal("100.00"));
        product.setId(1L);
        return product;
    }
}
