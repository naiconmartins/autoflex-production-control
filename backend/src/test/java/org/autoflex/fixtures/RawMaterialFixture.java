package org.autoflex.fixtures;

import java.math.BigDecimal;
import org.autoflex.application.commands.ProductCommand;
import org.autoflex.application.commands.RawMaterialCommand;
import org.autoflex.domain.RawMaterial;

public class RawMaterialFixture {

  public static ProductCommand.RawMaterialItem createListRawMaterialItem() {
    return new ProductCommand.RawMaterialItem(1L, new BigDecimal("150.00"));
  }

  public static RawMaterial createRawMaterial(Long id) {
    RawMaterial rawMaterial = new RawMaterial("RAW001", "MDF Board", new BigDecimal("1000.00"));
    rawMaterial.setId(id);
    return rawMaterial;
  }

  public static RawMaterialCommand createValidRawMaterialCommand() {
    return new RawMaterialCommand("RAW001", "MDF Board", new BigDecimal("1000.00"));
  }
}
