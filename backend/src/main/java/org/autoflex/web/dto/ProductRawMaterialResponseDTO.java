package org.autoflex.web.dto;

import lombok.Getter;
import lombok.Setter;
import org.autoflex.domain.entities.ProductRawMaterial;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRawMaterialResponseDTO {

    public Long id;
    public String code;
    public String name;
    public BigDecimal requiredQuantity;

    public ProductRawMaterialResponseDTO() {
    }

    public ProductRawMaterialResponseDTO(Long rawMaterialId, String rawMaterialCode, String rawMaterialName, BigDecimal requiredQuantity) {
        this.id = rawMaterialId;
        this.code = rawMaterialCode;
        this.name = rawMaterialName;
        this.requiredQuantity = requiredQuantity;
    }

    public ProductRawMaterialResponseDTO(ProductRawMaterial entity) {
        this.id = entity.getRawMaterial().getId();
        this.code = entity.getRawMaterial().getCode();
        this.name = entity.getRawMaterial().getName();
        this.requiredQuantity = entity.getRequiredQuantity();
    }
}
