package org.autoflex.adapters.inbound.dto.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.autoflex.domain.RawMaterial;
import org.autoflex.fixtures.RawMaterialFixture;
import org.junit.jupiter.api.Test;

class RawMaterialResponseDTOTest {

  @Test
  void constructor_shouldMapFields_whenCreatedFromDomain() {
    RawMaterial rawMaterial = RawMaterialFixture.createRawMaterial(1L);

    RawMaterialResponseDTO dto = new RawMaterialResponseDTO(rawMaterial);

    assertEquals(1L, dto.id);
    assertEquals("RAW001", dto.code);
    assertEquals("MDF Board", dto.name);
    assertEquals(0, new BigDecimal("1000.00").compareTo(dto.stockQuantity));
  }
}
