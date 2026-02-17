package org.autoflex.adapters.inbound.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import org.autoflex.adapters.inbound.dto.response.ProductionPlanResponseDTO;
import org.autoflex.domain.ProductionCapacity;
import org.autoflex.domain.ProductionPlan;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ProductionCapacityMapperTest {

  private final ProductionCapacityMapper mapper = Mappers.getMapper(ProductionCapacityMapper.class);

  @Test
  void toDomain_shouldMapProductionPlanToResponseDto() {
    ProductionCapacity item =
        new ProductionCapacity(
            1L,
            "PROD-001",
            "Dining Table",
            new BigDecimal("1200.00"),
            new BigDecimal("5.00"),
            new BigDecimal("6000.00"));
    ProductionPlan plan = new ProductionPlan(List.of(item), new BigDecimal("6000.00"));

    ProductionPlanResponseDTO response = mapper.toDomain(plan);

    assertEquals(1, response.items.size());
    assertEquals(1L, response.items.getFirst().productId);
    assertEquals("PROD-001", response.items.getFirst().productCode);
    assertEquals(0, new BigDecimal("6000.00").compareTo(response.grandTotalValue));
  }
}
