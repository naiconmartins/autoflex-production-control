package org.autoflex.web.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductionPlanResponseDTO {

    public List<ProductionPlanItemDTO> items = new ArrayList<>();
    public BigDecimal grandTotalValue = BigDecimal.ZERO;

    public ProductionPlanResponseDTO() {
    }

    public ProductionPlanResponseDTO(List<ProductionPlanItemDTO> items, BigDecimal grandTotalValue) {
        this.items = items;
        this.grandTotalValue = grandTotalValue;
    }
}
