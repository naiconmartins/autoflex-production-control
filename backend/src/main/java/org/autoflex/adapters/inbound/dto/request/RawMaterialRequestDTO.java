package org.autoflex.adapters.inbound.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class RawMaterialRequestDTO {
  @NotBlank(message = "Raw material code is required")
  public String code;

  @NotBlank(message = "Raw material name is required")
  public String name;

  @NotNull(message = "Stock quantity is required")
  @DecimalMin(value = "0.0", message = "Stock quantity cannot be negative")
  public BigDecimal stockQuantity;

  public RawMaterialRequestDTO() {}

  public RawMaterialRequestDTO(String code, String name, BigDecimal stockQuantity) {
    this.code = code;
    this.name = name;
    this.stockQuantity = stockQuantity;
  }
}
