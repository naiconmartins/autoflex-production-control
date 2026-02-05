package org.autoflex.web.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class RawMaterialRequestDTO {
    @NotBlank(message = "Raw material code is required")
    public String code;

    @NotBlank(message = "Raw material name is required")
    public String name;

    @NotNull(message = "Stock quantity is required")
    @DecimalMin(value = "0.0", message = "Stock quantity cannot be negative")
    public BigDecimal stockQuantity;
}
