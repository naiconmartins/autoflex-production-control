package org.autoflex.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.autoflex.entity.ProductRawMaterial;

import java.math.BigDecimal;
import java.util.List;

public class ProductRequestDTO {

    @NotBlank(message = "Product code is required")
    public String code;

    @NotBlank(message = "Product name is required")
    public String name;

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    public BigDecimal price;

    public List<ProductRawMaterialRequestDTO> rawMaterials;
}
