package org.autoflex.web.dto;

import lombok.Getter;
import org.autoflex.domain.entities.RawMaterial;

import java.math.BigDecimal;

@Getter
public class RawMaterialResponseDTO {
    public Long id;
    public String code;
    public String name;
    public BigDecimal stockQuantity;

    public RawMaterialResponseDTO(Long id, String code, String name, BigDecimal stockQuantity) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.stockQuantity = stockQuantity;
    }

    public RawMaterialResponseDTO(RawMaterial entity) {
        this.code = entity.getCode();
        this.name = entity.getName();
        this.stockQuantity = entity.getStockQuantity();
    }
}
