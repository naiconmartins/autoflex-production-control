package org.autoflex.application.commands;

import java.math.BigDecimal;
import org.autoflex.common.exceptions.InvalidDataException;

public record RawMaterialCommand(String code, String name, BigDecimal stockQuantity) {

  public RawMaterialCommand {
    if (code == null || code.isBlank()) {
      throw new InvalidDataException("Raw material code is required");
    }
    if (name == null || name.isBlank()) {
      throw new InvalidDataException("Raw material name is required");
    }
    if (stockQuantity == null) {
      throw new InvalidDataException("Stock quantity is required");
    }
    if (stockQuantity.compareTo(BigDecimal.ZERO) < 0) {
      throw new InvalidDataException("Stock quantity cannot be negative");
    }
  }
}
