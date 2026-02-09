package org.autoflex.web.dto;

import lombok.Getter;
import lombok.Setter;
import org.autoflex.domain.entities.ProductRawMaterial;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRawMaterialResponseDTO {

    public Long rawMaterialId;
    public String rawMaterialCode;
    public String rawMaterialName;
    public BigDecimal requiredQuantity;

    public ProductRawMaterialResponseDTO() {
    }

    public ProductRawMaterialResponseDTO(Long rawMaterialId, String rawMaterialCode, String rawMaterialName, BigDecimal requiredQuantity) {
        this.rawMaterialId = rawMaterialId;
        this.rawMaterialCode = rawMaterialCode;
        this.rawMaterialName = rawMaterialName;
        this.requiredQuantity = requiredQuantity;
    }

    public ProductRawMaterialResponseDTO(ProductRawMaterial entity) {
        this.rawMaterialId = entity.getRawMaterial().getId();
        this.rawMaterialCode = entity.getRawMaterial().getCode();
        this.rawMaterialName = entity.getRawMaterial().getName();
        this.requiredQuantity = entity.getRequiredQuantity();
    }
}
