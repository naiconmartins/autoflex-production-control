package org.autoflex.adapters.inbound.dto.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.autoflex.domain.Product;
import org.autoflex.fixtures.ProductFixture;
import org.autoflex.fixtures.RawMaterialFixture;
import org.junit.jupiter.api.Test;

class ProductResponseDTOTest {

  @Test
  void constructor_shouldMapFields_whenCreatedFromDomain() {
    Product product = ProductFixture.createProduct();
    product.setId(10L);
    product.setCode("PROD-010");
    product.setName("Desk");
    product.setPrice(new BigDecimal("250.00"));
    product.addRawMaterial(RawMaterialFixture.createRawMaterial(1L), new BigDecimal("2.00"));

    ProductResponseDTO dto = new ProductResponseDTO(product);

    assertEquals(10L, dto.id);
    assertEquals("PROD-010", dto.code);
    assertEquals("Desk", dto.name);
    assertEquals(0, new BigDecimal("250.00").compareTo(dto.price));
    assertEquals(1, dto.rawMaterials.size());
  }
}
