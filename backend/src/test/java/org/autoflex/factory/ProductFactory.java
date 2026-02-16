package org.autoflex.factory;

import java.math.BigDecimal;
import java.util.List;
import org.autoflex.adapters.inbound.dto.request.ProductRawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.request.ProductRequestDTO;
import org.autoflex.adapters.inbound.dto.response.ProductRawMaterialResponseDTO;
import org.autoflex.adapters.inbound.dto.response.ProductResponseDTO;
import org.autoflex.domain.Product;

public class ProductFactory {

  public static ProductRequestDTO createProductRequestDTO() {
    return new ProductRequestDTO("PROD001", "Product Test", new BigDecimal("100.00"), List.of());
  }

  public static ProductRequestDTO createProductRequestDTOWithRawMaterials() {
    return createProductRequestDTOWithRawMaterials("PROD-040");
  }

  public static ProductRequestDTO createProductRequestDTOWithRawMaterials(String code) {
    return new ProductRequestDTO(
        code,
        "Product Test",
        new BigDecimal("100.00"),
        List.of(
            new ProductRawMaterialRequestDTO(1L, new BigDecimal("10.00")),
            new ProductRawMaterialRequestDTO(2L, new BigDecimal("5.00"))));
  }

  public static ProductRequestDTO createUniqueProductRequestDTOWithRawMaterials() {
    return createProductRequestDTOWithRawMaterials(TestData.unique("PROD-IT"));
  }

  public static ProductResponseDTO createProductResponseDTOWithRawMaterials() {
    return new ProductResponseDTO(
        1L,
        "PROD001",
        "Product Test",
        new BigDecimal("100.00"),
        List.of(
            new ProductRawMaterialResponseDTO(
                1L, "RAW001", "Raw Material 1", new BigDecimal("10.00")),
            new ProductRawMaterialResponseDTO(
                2L, "RAW002", "Raw Material 2", new BigDecimal("5.00"))));
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
