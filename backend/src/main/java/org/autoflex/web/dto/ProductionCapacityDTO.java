package org.autoflex.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProductionPlanItemDTO {

    public Long productId;
    public String productCode;
    public String productName;
    public BigDecimal unitPrice;
    public BigDecimal producibleQuantity;
    public BigDecimal totalValue;

    public ProductionPlanItemDTO() {
    }

    public ProductionPlanItemDTO(Long productId, String productCode, String productName, BigDecimal unitPrice, BigDecimal producibleQuantity, BigDecimal totalValue) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.producibleQuantity = producibleQuantity;
        this.totalValue = totalValue;
    }
}