package org.autoflex.adapters.inbound.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import org.autoflex.adapters.inbound.dto.request.RawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.response.PageResponseDTO;
import org.autoflex.adapters.inbound.dto.response.RawMaterialResponseDTO;
import org.autoflex.application.commands.RawMaterialCommand;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.domain.RawMaterial;
import org.autoflex.fixtures.RawMaterialFixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class RawMaterialMapperTest {

  private final RawMaterialMapper mapper = Mappers.getMapper(RawMaterialMapper.class);

  @Test
  void toCommand_shouldMapRawMaterialRequestDtoToCommand() {
    RawMaterialRequestDTO dto = new RawMaterialRequestDTO("RAW-001", "MDF", new BigDecimal("10.00"));

    RawMaterialCommand command = mapper.toCommand(dto);

    assertEquals(dto.code, command.code());
    assertEquals(dto.name, command.name());
    assertEquals(0, dto.stockQuantity.compareTo(command.stockQuantity()));
  }

  @Test
  void toResponse_shouldMapPagedModelToPageResponse() {
    RawMaterial rawMaterial = RawMaterialFixture.createRawMaterial(1L);
    rawMaterial.setCode("RAW-001");
    rawMaterial.setName("MDF Board");
    rawMaterial.setStockQuantity(new BigDecimal("150.00"));
    PagedModel<RawMaterial> model = new PagedModel<>(List.of(rawMaterial), 1L, 1);

    PageResponseDTO<RawMaterialResponseDTO> response = mapper.toResponse(model, 0, 10);

    assertEquals(1L, response.totalElements);
    assertEquals(1, response.totalPages);
    assertEquals(0, response.page);
    assertEquals(10, response.size);
    assertEquals(1, response.content.size());
    assertEquals("RAW-001", response.content.getFirst().code);
  }
}
