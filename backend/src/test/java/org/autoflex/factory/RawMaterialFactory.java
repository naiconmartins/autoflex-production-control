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

    public static RawMaterial createCustomRawMaterial(Long id) {
        RawMaterial entity = new RawMaterial("RAW001", "RawTest", new  BigDecimal("100.00"));
        entity.setId(id);
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
        return new RawMaterial(code, "Screwdriver", new BigDecimal("50.00"));
    }
}
