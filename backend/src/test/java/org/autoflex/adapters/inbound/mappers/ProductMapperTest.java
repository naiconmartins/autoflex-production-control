package org.autoflex.adapters.inbound.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;
import java.util.List;
import org.autoflex.adapters.inbound.dto.request.ProductRawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.request.ProductRequestDTO;
import org.autoflex.adapters.inbound.dto.response.PageResponseDTO;
import org.autoflex.adapters.inbound.dto.response.ProductResponseDTO;
import org.autoflex.application.commands.ProductCommand;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.domain.Product;
import org.autoflex.fixtures.ProductFixture;
import org.autoflex.fixtures.RawMaterialFixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ProductMapperTest {

  private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

  @Test
  void toCommand_shouldMapProductRequestDtoToCommand() {
    ProductRequestDTO dto =
        new ProductRequestDTO(
            "PROD-TEST",
            "Product Test",
            new BigDecimal("100.00"),
            List.of(new ProductRawMaterialRequestDTO(1L, new BigDecimal("2.50"))));

    ProductCommand command = mapper.toCommand(dto);

    assertEquals(dto.code, command.code());
    assertEquals(dto.name, command.name());
    assertEquals(0, dto.price.compareTo(command.price()));
    assertEquals(1, command.rawMaterials().size());
    assertEquals(1L, command.rawMaterials().getFirst().rawMaterialId());
    assertEquals(
        0, new BigDecimal("2.50").compareTo(command.rawMaterials().getFirst().requiredQuantity()));
  }

  @Test
  void toItem_shouldMapProductRawMaterialRequestDtoToItem() {
    ProductRawMaterialRequestDTO dto = new ProductRawMaterialRequestDTO(1L, new BigDecimal("3.00"));

    ProductCommand.RawMaterialItem item = mapper.toItem(dto);

    assertEquals(dto.id, item.rawMaterialId());
    assertEquals(0, dto.requiredQuantity.compareTo(item.requiredQuantity()));
  }

  @Test
  void toResponse_shouldMapPagedModelToPageResponse() {
    Product product = ProductFixture.createProduct();
    product.setId(10L);
    product.setCode("PROD-010");
    product.setName("Desk Test");
    product.addRawMaterial(RawMaterialFixture.createRawMaterial(1L), new BigDecimal("1.50"));

    PagedModel<Product> model = new PagedModel<>(List.of(product), 1L, 1);

    PageResponseDTO<ProductResponseDTO> response = mapper.toResponse(model, 0, 10);

    assertEquals(1L, response.totalElements);
    assertEquals(1, response.totalPages);
    assertEquals(0, response.page);
    assertEquals(10, response.size);
    assertEquals(1, response.content.size());
    assertEquals("PROD-010", response.content.getFirst().code);
    assertFalse(response.content.getFirst().rawMaterials.isEmpty());
  }
}
