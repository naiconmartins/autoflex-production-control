package org.autoflex.application.commands;

import org.autoflex.common.exceptions.InvalidDataException;

import java.math.BigDecimal;
import java.util.List;

public record ProductCommand(
    String code, String name, BigDecimal price, List<RawMaterialItem> rawMaterials) {

  public ProductCommand {
    if (code == null || code.isBlank()) {
      throw new InvalidDataException("Product code is required");
    }
    if (name == null || name.isBlank()) {
      throw new InvalidDataException("Product name is required");
    }
    if (price == null) {
      throw new InvalidDataException("Product price is required");
    }
    if (price.compareTo(new BigDecimal("0.01")) < 0) {
      throw new InvalidDataException("Price must be greater than zero");
    }
    if (rawMaterials != null) {
      for (RawMaterialItem item : rawMaterials) {
        if (item == null) {
          throw new InvalidDataException("Raw material item is required");
        }
        if (item.rawMaterialId() == null) {
          throw new InvalidDataException("Raw material id is required");
        }
        if (item.requiredQuantity() == null) {
          throw new InvalidDataException("Required quantity is required");
        }
        if (item.requiredQuantity().compareTo(new BigDecimal("0.01")) < 0) {
          throw new InvalidDataException("Required quantity must be greater than zero");
        }
      }
    }
  }

  public record RawMaterialItem(Long rawMaterialId, BigDecimal requiredQuantity) {}
}
