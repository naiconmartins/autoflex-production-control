package org.autoflex.application.commands;

import java.math.BigDecimal;

public record RawMaterialCommand(String code, String name, BigDecimal stockQuantity) {}
