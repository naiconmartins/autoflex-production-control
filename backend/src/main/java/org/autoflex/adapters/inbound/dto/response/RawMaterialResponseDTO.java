package org.autoflex.adapters.inbound.dto.response;

import java.math.BigDecimal;
import lombok.Getter;
import org.autoflex.domain.RawMaterial;

@Getter
public class RawMaterialResponseDTO {
  public Long id;
  public String code;
  public String name;
  public BigDecimal stockQuantity;

  public RawMaterialResponseDTO() {}

  public RawMaterialResponseDTO(Long id, String code, String name, BigDecimal stockQuantity) {
    this.id = id;
    this.code = code;
    this.name = name;
    this.stockQuantity = stockQuantity;
  }

  public RawMaterialResponseDTO(RawMaterial entity) {
    this.id = entity.getId();
    this.code = entity.getCode();
    this.name = entity.getName();
    this.stockQuantity = entity.getStockQuantity();
  }
}
