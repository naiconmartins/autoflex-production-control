package org.autoflex.web.dto;

import org.autoflex.domain.entities.ProductRawMaterial;

import java.math.BigDecimal;

public class ProductRawMaterialResponseDTO {
    public Long id;
    public String code;
    public String name;
    public BigDecimal requiredQuantity;

    public ProductRawMaterialResponseDTO() {}

    public ProductRawMaterialResponseDTO(Long id, String code, String name, BigDecimal requiredQuantity) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.requiredQuantity = requiredQuantity;
    }

    public ProductRawMaterialResponseDTO(ProductRawMaterial entity) {
        this.id = entity.getRawMaterial().getId();
        this.code = entity.getRawMaterial().getCode();
        this.name = entity.getRawMaterial().getName();
        this.requiredQuantity = entity.getRequiredQuantity();
    }
}
