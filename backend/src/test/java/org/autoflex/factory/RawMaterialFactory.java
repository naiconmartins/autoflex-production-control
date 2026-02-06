package org.autoflex.factory;

import org.autoflex.domain.entities.RawMaterial;
import org.autoflex.web.dto.RawMaterialRequestDTO;

import java.math.BigDecimal;

public class RawMaterialFactory {
    public static RawMaterialRequestDTO createRawMaterialRequestDTO() {
        RawMaterialRequestDTO dto = new RawMaterialRequestDTO();
        dto.code = "RM001";
        dto.name = "Steel Sheet";
        dto.stockQuantity = new BigDecimal("100.00");
        return dto;
    }

    public static RawMaterial createCustomRawMaterial(
            String code,
            String name,
            BigDecimal stockQuantity) {
        RawMaterial entity = new RawMaterial();
        entity.setCode(code);
        entity.setName(name);
        entity.setStockQuantity(stockQuantity);
        return entity;
    }

    public static RawMaterialRequestDTO createInvalidRawMaterialRequestDTO() {
        RawMaterialRequestDTO dto = new RawMaterialRequestDTO();
        dto.code = "";
        dto.name = "";
        dto.stockQuantity = new BigDecimal("-1.00");
        return dto;
    }

    public static RawMaterial createRawMaterialWithCode(String code) {
        RawMaterial entity = new RawMaterial();
        entity.setCode(code);
        entity.setName("Screwdriver");
        entity.setStockQuantity(new BigDecimal("50.00"));
        return entity;
    }
}
