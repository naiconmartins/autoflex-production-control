package org.autoflex.application.commands;

import java.math.BigDecimal;

public record ProductRawMaterialCommand(Long rawMaterialId, BigDecimal requiredQuantity) {}
