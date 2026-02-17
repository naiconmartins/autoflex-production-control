package org.autoflex.adapters.inbound.dto.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProductionPlanResponseDTOTest {

  @Test
  void constructor_shouldInitializeDefaults_whenCreatedWithoutArgs() {
    ProductionPlanResponseDTO dto = new ProductionPlanResponseDTO();
    assertNotNull(dto.items);
    assertTrue(dto.items.isEmpty());
    assertEquals(0, BigDecimal.ZERO.compareTo(dto.grandTotalValue));
  }

  @Test
  void constructor_shouldSetFields_whenAllArgsAreProvided() {
    ProductionPlanResponseDTO dto =
        new ProductionPlanResponseDTO(
            List.of(
                new ProductionCapacityDTO(
                    1L,
                    "PROD-001",
                    "Dining Table",
                    new BigDecimal("100.00"),
                    new BigDecimal("2.00"),
                    new BigDecimal("200.00"))),
            new BigDecimal("200.00"));

    assertEquals(1, dto.items.size());
    assertEquals("PROD-001", dto.items.getFirst().productCode);
    assertEquals(0, new BigDecimal("200.00").compareTo(dto.grandTotalValue));
  }
}
