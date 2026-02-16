package org.autoflex.adapters.inbound.dto.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductionPlanResponseDTO {

  public List<ProductionCapacityDTO> items = new ArrayList<>();
  public BigDecimal grandTotalValue = BigDecimal.ZERO;

  public ProductionPlanResponseDTO() {}

  public ProductionPlanResponseDTO(List<ProductionCapacityDTO> items, BigDecimal grandTotalValue) {
    this.items = items;
    this.grandTotalValue = grandTotalValue;
  }
}
