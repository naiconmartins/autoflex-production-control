package org.autoflex.adapters.inbound.mappers;

import java.util.List;
import org.autoflex.adapters.inbound.dto.request.ProductRawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.response.ProductRawMaterialResponseDTO;
import org.autoflex.application.commands.ProductRawMaterialCommand;
import org.autoflex.domain.ProductRawMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface ProductRawMaterialMapper {

  @Mapping(source = "id", target = "rawMaterialId")
  @Mapping(source = "requiredQuantity", target = "requiredQuantity")
  ProductRawMaterialCommand toCommand(ProductRawMaterialRequestDTO dto);

  ProductRawMaterialResponseDTO toResponse(ProductRawMaterial dto);

  List<ProductRawMaterialResponseDTO> toList(List<ProductRawMaterial> list);

  default ProductRawMaterialResponseDTO map(ProductRawMaterial prm) {
    return new ProductRawMaterialResponseDTO(prm);
  }
}
