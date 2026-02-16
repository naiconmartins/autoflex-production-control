package org.autoflex.adapters.inbound.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.autoflex.domain.Product;

@Getter
public class ProductResponseDTO {

  public Long id;
  public String code;
  public String name;
  public BigDecimal price;
  public List<ProductRawMaterialResponseDTO> rawMaterials;

  public ProductResponseDTO() {}

  public ProductResponseDTO(
      Long id,
      String code,
      String name,
      BigDecimal price,
      List<ProductRawMaterialResponseDTO> rawMaterials) {
    this.id = id;
    this.code = code;
    this.name = name;
    this.price = price;
    this.rawMaterials = rawMaterials;
  }

  public ProductResponseDTO(Product entity) {
    this.id = entity.getId();
    this.code = entity.getCode();
    this.name = entity.getName();
    this.price = entity.getPrice();
    this.rawMaterials =
        entity.getRawMaterials().stream()
            .map(ProductRawMaterialResponseDTO::new)
            .collect(Collectors.toList());
  }
}
