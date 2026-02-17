package org.autoflex.application.commands;

import java.math.BigDecimal;
import org.autoflex.common.exceptions.InvalidDataException;

public record ProductRawMaterialCommand(Long rawMaterialId, BigDecimal requiredQuantity) {

  public ProductRawMaterialCommand {
    if (rawMaterialId == null) {
      throw new InvalidDataException("Raw material id is required");
    }
    if (requiredQuantity == null) {
      throw new InvalidDataException("Required quantity is required");
    }
    if (requiredQuantity.compareTo(new BigDecimal("0.01")) < 0) {
      throw new InvalidDataException("Required quantity must be greater than zero");
    }
  }
}
