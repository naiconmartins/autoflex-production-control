package org.autoflex.application.usecases;

import java.util.List;
import org.autoflex.application.commands.ProductRawMaterialCommand;
import org.autoflex.domain.ProductRawMaterial;

public interface ProductRawMaterialUseCase {

  ProductRawMaterial add(Long productId, ProductRawMaterialCommand cmd);

  List<ProductRawMaterial> listByProduct(Long productId);

  ProductRawMaterial updateRequiredQuantity(
      Long productId, Long rawMaterialId, ProductRawMaterialCommand dto);

  void remove(Long productId, Long rawMaterialId);
}
