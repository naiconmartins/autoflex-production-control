package org.autoflex.fixtures;

import java.math.BigDecimal;
import java.util.List;
import org.autoflex.application.commands.ProductCommand;
import org.autoflex.domain.Product;

public class ProductFixture {

  public static Product createProduct() {
    return new Product("PROD001", "Desk with 4 Drawers", new BigDecimal("599.90"));
  }

  public static ProductCommand createValidProductCommand() {
    return new ProductCommand(
        "PROD002",
        "Dining Table Oak 2.00m",
        new BigDecimal("1850.00"),
        List.of(RawMaterialFixture.createListRawMaterialItem()));
  }
}
