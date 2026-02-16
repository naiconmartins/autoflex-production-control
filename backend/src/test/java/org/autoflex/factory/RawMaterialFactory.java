package org.autoflex.factory;

import java.math.BigDecimal;
import org.autoflex.adapters.inbound.dto.request.RawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.response.RawMaterialResponseDTO;
import org.autoflex.domain.RawMaterial;

public class RawMaterialFactory {
  public static RawMaterialRequestDTO createRawMaterialRequestDTO() {
    RawMaterialRequestDTO dto = new RawMaterialRequestDTO();
    dto.code = "RM001";
    dto.name = "Steel Sheet";
    dto.stockQuantity = new BigDecimal("100.00");
    return dto;
  }

  public static RawMaterialRequestDTO createUniqueRawMaterialRequestDTO() {
    RawMaterialRequestDTO dto = createRawMaterialRequestDTO();
    dto.code = TestData.unique("RM-IT");
    dto.name = "Raw Material IT";
    return dto;
  }

  public static RawMaterial createCustomRawMaterial(Long id) {
    RawMaterial entity = new RawMaterial("RAW001", "RawTest", new BigDecimal("100.00"));
    entity.setId(id);
    return entity;
  }

  public static RawMaterial createRawMaterialWithCode(String code) {
    return new RawMaterial(code, "Screwdriver", new BigDecimal("50.00"));
  }

  public static RawMaterialResponseDTO createRawMaterialResponseDTO() {
    return new RawMaterialResponseDTO(1L, "RAW001", "Raw Material Test", new BigDecimal("150.00"));
  }
}
