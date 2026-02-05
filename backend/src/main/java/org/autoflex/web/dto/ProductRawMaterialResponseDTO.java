package org.autoflex.web.dto;

import org.autoflex.domain.entities.ProductRawMaterial;

import java.math.BigDecimal;

public class ProductRawMaterialResponseDTO {
    public Long rawMaterialId;
    public String rawMaterialCode;
    public String rawMaterialName;
    public BigDecimal requiredQuantity;

    public ProductRawMaterialResponseDTO(ProductRawMaterial entity) {
        this.rawMaterialId = entity.getRawMaterial().getId();
        this.rawMaterialCode = entity.getRawMaterial().getCode();
        this.rawMaterialName = entity.getRawMaterial().getName();
        this.requiredQuantity = entity.getRequiredQuantity();
    }
}
