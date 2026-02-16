package org.autoflex.factory;

import java.math.BigDecimal;
import java.util.List;
import org.autoflex.adapters.inbound.dto.request.ProductRawMaterialRequestDTO;
import org.autoflex.domain.Product;
import org.autoflex.domain.ProductRawMaterial;
import org.autoflex.domain.RawMaterial;

public class ProductRawMaterialFactory {

  public static Product createProduct(Long id, String code) {
    Product product = new Product(code, "Product " + code, new BigDecimal("100.00"));
    product.setId(id);
    return product;
  }

  public static RawMaterial createRawMaterial(Long id, String code) {
    RawMaterial rawMaterial = new RawMaterial(code, "Raw " + code, new BigDecimal("100.00"));
    rawMaterial.setId(id);
    return rawMaterial;
  }

  public static ProductRawMaterial createLink(
      Product product, RawMaterial rawMaterial, BigDecimal requiredQuantity) {
    ProductRawMaterial link = new ProductRawMaterial(product, rawMaterial, requiredQuantity);
    link.setId(1L);
    return link;
  }

  public static ProductRawMaterialRequestDTO createRequest(
      Long rawMaterialId, BigDecimal requiredQuantity) {
    return new ProductRawMaterialRequestDTO(rawMaterialId, requiredQuantity);
  }

  public static List<ProductRawMaterialRequestDTO> createListOfRawMaterialsRequestDTO() {
    return List.of(
        new ProductRawMaterialRequestDTO(1L, new BigDecimal("10.00")),
        new ProductRawMaterialRequestDTO(2L, new BigDecimal("5.00")));
  }
}
