package org.autoflex.adapters.inbound.dto.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.autoflex.domain.ProductRawMaterial;
import org.autoflex.fixtures.ProductRawMaterialFixture;
import org.junit.jupiter.api.Test;

class ProductRawMaterialResponseDTOTest {

  @Test
  void constructor_shouldMapFields_whenCreatedFromDomain() {
    ProductRawMaterial link =
        ProductRawMaterialFixture.createLink(1L, 1L, 2L, new BigDecimal("5.00"));

    ProductRawMaterialResponseDTO dto = new ProductRawMaterialResponseDTO(link);

    assertEquals(2L, dto.id);
    assertEquals("RAW001", dto.code);
    assertEquals("MDF Board", dto.name);
    assertEquals(0, new BigDecimal("5.00").compareTo(dto.requiredQuantity));
  }
}
