package org.autoflex.fixtures;

import java.math.BigDecimal;
import org.autoflex.application.commands.ProductRawMaterialCommand;
import org.autoflex.domain.Product;
import org.autoflex.domain.ProductRawMaterial;
import org.autoflex.domain.RawMaterial;

public class ProductRawMaterialFixture {

  public static Product createProduct(Long id) {
    Product product = ProductFixture.createProduct();
    product.setId(id);
    return product;
  }

  public static RawMaterial createRawMaterial(Long id) {
    return RawMaterialFixture.createRawMaterial(id);
  }

  public static ProductRawMaterialCommand createCommand(
      Long rawMaterialId, BigDecimal requiredQuantity) {
    return new ProductRawMaterialCommand(rawMaterialId, requiredQuantity);
  }

  public static ProductRawMaterial createLink(
      Long id, Long productId, Long rawMaterialId, BigDecimal requiredQuantity) {
    Product product = createProduct(productId);
    RawMaterial rawMaterial = createRawMaterial(rawMaterialId);

    ProductRawMaterial link = new ProductRawMaterial(product, rawMaterial, requiredQuantity);
    link.setId(id);
    return link;
  }
}
