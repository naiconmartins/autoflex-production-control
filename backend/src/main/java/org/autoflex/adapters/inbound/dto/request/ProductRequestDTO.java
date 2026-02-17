package org.autoflex.adapters.inbound.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

@Getter
public class ProductRequestDTO {

  @NotBlank(message = "Product code is required")
  public String code;

  @NotBlank(message = "Product name is required")
  public String name;

  @NotNull(message = "Product price is required")
  @DecimalMin(value = "0.01", message = "Price must be greater than zero")
  public BigDecimal price;

  @NotEmpty(message = "At least one raw material is required")
  public List<@Valid ProductRawMaterialRequestDTO> rawMaterials;

  ProductRequestDTO() {}

  public ProductRequestDTO(
      String code, String name, BigDecimal price, List<ProductRawMaterialRequestDTO> rawMaterials) {
    this.code = code;
    this.name = name;
    this.price = price;
    this.rawMaterials = rawMaterials;
  }
}
