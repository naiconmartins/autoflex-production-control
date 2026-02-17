package org.autoflex.adapters.inbound.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import org.autoflex.adapters.inbound.dto.request.ProductRawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.response.ProductRawMaterialResponseDTO;
import org.autoflex.application.commands.ProductRawMaterialCommand;
import org.autoflex.domain.ProductRawMaterial;
import org.autoflex.fixtures.ProductRawMaterialFixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ProductRawMaterialMapperTest {

  private final ProductRawMaterialMapper mapper = Mappers.getMapper(ProductRawMaterialMapper.class);

  @Test
  void toCommand_shouldMapRequestDtoToCommand() {
    ProductRawMaterialRequestDTO dto = new ProductRawMaterialRequestDTO(2L, new BigDecimal("4.00"));

    ProductRawMaterialCommand command = mapper.toCommand(dto);

    assertEquals(dto.id, command.rawMaterialId());
    assertEquals(0, dto.requiredQuantity.compareTo(command.requiredQuantity()));
  }

  @Test
  void toList_shouldMapDomainListToResponseList() {
    ProductRawMaterial first =
        ProductRawMaterialFixture.createLink(1L, 1L, 1L, new BigDecimal("2.00"));
    ProductRawMaterial second =
        ProductRawMaterialFixture.createLink(2L, 1L, 2L, new BigDecimal("3.50"));

    List<ProductRawMaterialResponseDTO> response = mapper.toList(List.of(first, second));

    assertEquals(2, response.size());
    assertEquals(1L, response.get(0).id);
    assertEquals(0, new BigDecimal("2.00").compareTo(response.get(0).requiredQuantity));
    assertEquals(2L, response.get(1).id);
    assertEquals(0, new BigDecimal("3.50").compareTo(response.get(1).requiredQuantity));
  }
}
