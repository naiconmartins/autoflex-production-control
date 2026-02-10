package org.autoflex.web.dto;

import java.math.BigDecimal;

public class ProductionCapacityDTO {

    public Long productId;
    public String productCode;
    public String productName;
    public BigDecimal unitPrice;
    public BigDecimal producibleQuantity;
    public BigDecimal totalValue;

    public ProductionCapacityDTO() {
    }

    public ProductionCapacityDTO(Long productId, String productCode, String productName, BigDecimal unitPrice, BigDecimal producibleQuantity, BigDecimal totalValue) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.producibleQuantity = producibleQuantity;
        this.totalValue = totalValue;
    }
}