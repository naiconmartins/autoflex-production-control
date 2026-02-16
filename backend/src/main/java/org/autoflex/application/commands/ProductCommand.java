package org.autoflex.application.commands;

import java.math.BigDecimal;
import java.util.List;

public record ProductCommand(
    String code, String name, BigDecimal price, List<RawMaterialItem> rawMaterials) {

  public record RawMaterialItem(Long rawMaterialId, BigDecimal requiredQuantity) {}
}
