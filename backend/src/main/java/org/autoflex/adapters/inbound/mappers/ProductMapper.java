package org.autoflex.adapters.inbound.mappers;

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

  ProductCommand toInsertCommand(ProductRequestDTO dto);

  @Mapping(source = "id", target = "rawMaterialId")
  @Mapping(source = "requiredQuantity", target = "requiredQuantity")
  ProductCommand.RawMaterialItem toItem(ProductRawMaterialRequestDTO dto);

  @Mapping(target = "content", source = "model.items")
  @Mapping(target = "totalElements", source = "model.totalElements")
  @Mapping(target = "totalPages", source = "model.totalPages")
  @Mapping(target = "page", source = "page")
  @Mapping(target = "size", source = "size")
  PageResponseDTO<ProductResponseDTO> toResponse(PagedModel<Product> model, int page, int size);

  default ProductResponseDTO map(Product product) {
    return new ProductResponseDTO(product);
  }
}
