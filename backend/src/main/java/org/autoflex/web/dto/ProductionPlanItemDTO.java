package org.autoflex.web.dto;

import java.math.BigDecimal;

public class ProductionPlanItemDTO {

    public Long productId;
    public String productCode;
    public String productName;
    public BigDecimal unitPrice;
    public Long producibleQuantity;
    public BigDecimal totalValue;

    public ProductionPlanItemDTO() {
    }

    public ProductionPlanItemDTO(Long productId, String productCode, String productName, BigDecimal unitPrice, Long producibleQuantity, BigDecimal totalValue) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.producibleQuantity = producibleQuantity;
        this.totalValue = totalValue;
    }
}