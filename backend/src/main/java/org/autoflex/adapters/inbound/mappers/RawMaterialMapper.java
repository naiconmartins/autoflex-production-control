package org.autoflex.adapters.inbound.mappers;

import java.util.List;
import org.autoflex.adapters.inbound.dto.request.RawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.response.PageResponseDTO;
import org.autoflex.adapters.inbound.dto.response.RawMaterialResponseDTO;
import org.autoflex.application.commands.RawMaterialCommand;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.domain.RawMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface RawMaterialMapper {

  RawMaterialCommand toCommand(RawMaterialRequestDTO dto);

  default PageResponseDTO<RawMaterialResponseDTO> toResponse(
      PagedModel<RawMaterial> model, int page, int size) {
    List<RawMaterialResponseDTO> content =
        model.items() == null ? List.of() : model.items().stream().map(this::map).toList();
    return new PageResponseDTO<>(content, model.totalElements(), model.totalPages(), page, size);
  }

  default RawMaterialResponseDTO map(RawMaterial rawMaterial) {
    return new RawMaterialResponseDTO(rawMaterial);
  }
}
