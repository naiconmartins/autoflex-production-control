package org.autoflex.adapters.inbound.dto.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductionCapacityDTOTest {

  @Test
  void constructor_shouldSetFields_whenAllArgsAreProvided() {
    ProductionCapacityDTO dto =
        new ProductionCapacityDTO(
            1L,
            "PROD-001",
            "Dining Table",
            new BigDecimal("100.00"),
            new BigDecimal("2.00"),
            new BigDecimal("200.00"));

    assertEquals(1L, dto.productId);
    assertEquals("PROD-001", dto.productCode);
    assertEquals(0, new BigDecimal("200.00").compareTo(dto.totalValue));
  }
}
