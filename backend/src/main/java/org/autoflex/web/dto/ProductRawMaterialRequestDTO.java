package org.autoflex.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProductRawMaterialRequestDTO {

    @NotNull(message = "Raw material id is required")
    public Long id;

    @NotNull(message = "Required quantity is required")
    @DecimalMin(value = "0.01", message = "Required quantity must be greater than zero")
    public BigDecimal requiredQuantity;

    public ProductRawMaterialRequestDTO(Long id, BigDecimal requiredQuantity) {
        this.id = id;
        this.requiredQuantity = requiredQuantity;
    }
}
