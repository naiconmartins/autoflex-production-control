package org.autoflex.adapters.inbound.mappers;

import org.autoflex.adapters.inbound.dto.request.RawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.response.PageResponseDTO;
import org.autoflex.adapters.inbound.dto.response.RawMaterialResponseDTO;
import org.autoflex.application.commands.RawMaterialCommand;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.domain.RawMaterial;
import org.mapstruct.Mapping;

public interface RawMaterialMapper {

  RawMaterialCommand toCommand(RawMaterialRequestDTO dto);

  @Mapping(target = "content", source = "model.items")
  @Mapping(target = "totalElements", source = "model.totalElements")
  @Mapping(target = "totalPages", source = "model.totalPages")
  @Mapping(target = "page", source = "page")
  @Mapping(target = "size", source = "size")
  PageResponseDTO<RawMaterialResponseDTO> toResponse(
      PagedModel<RawMaterial> model, int page, int size);

  default RawMaterialResponseDTO map(RawMaterial rawMaterial) {
    return new RawMaterialResponseDTO(rawMaterial);
  }
}
