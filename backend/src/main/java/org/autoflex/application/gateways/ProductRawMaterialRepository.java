package org.autoflex.application.gateways;

import java.util.List;
import java.util.Optional;
import org.autoflex.domain.ProductRawMaterial;

public interface ProductRawMaterialRepository {

  ProductRawMaterial save(ProductRawMaterial productRawMaterial);

  Optional<ProductRawMaterial> findById(Long id);

  Optional<ProductRawMaterial> findByProductAndRawMaterial(Long productId, Long rawMaterialId);

  List<ProductRawMaterial> listByProduct(Long productId);

  void delete(Long id);
}
