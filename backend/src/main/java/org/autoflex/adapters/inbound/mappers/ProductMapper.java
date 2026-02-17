package org.autoflex.adapters.inbound.mappers;

import java.util.List;
import org.autoflex.adapters.inbound.dto.request.ProductRawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.request.ProductRequestDTO;
import org.autoflex.adapters.inbound.dto.response.PageResponseDTO;
import org.autoflex.adapters.inbound.dto.response.ProductResponseDTO;
import org.autoflex.application.commands.ProductCommand;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface ProductMapper {

  ProductCommand toCommand(ProductRequestDTO dto);

  @Mapping(source = "id", target = "rawMaterialId")
  @Mapping(source = "requiredQuantity", target = "requiredQuantity")
  ProductCommand.RawMaterialItem toItem(ProductRawMaterialRequestDTO dto);

  default PageResponseDTO<ProductResponseDTO> toResponse(PagedModel<Product> model, int page, int size) {
    List<ProductResponseDTO> content =
        model.items() == null ? List.of() : model.items().stream().map(this::map).toList();
    return new PageResponseDTO<>(content, model.totalElements(), model.totalPages(), page, size);
  }

  default ProductResponseDTO map(Product product) {
    return new ProductResponseDTO(product);
  }
}
