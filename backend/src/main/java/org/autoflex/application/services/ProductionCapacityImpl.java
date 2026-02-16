package org.autoflex.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.autoflex.application.gateways.ProductRawMaterialRepository;
import org.autoflex.application.gateways.ProductRepository;
import org.autoflex.application.gateways.RawMaterialRepository;
import org.autoflex.application.usecases.ProductionCapacityUseCase;
import org.autoflex.domain.*;

@ApplicationScoped
public class ProductionCapacityImpl implements ProductionCapacityUseCase {

  @Inject ProductRepository productRepository;
  @Inject RawMaterialRepository rawMaterialRepository;
  @Inject ProductRawMaterialRepository productRawMaterialRepository;

  public ProductionPlan generate() {
    Map<Long, BigDecimal> remainingStock = loadStock();
    List<Product> products = productRepository.findAllOrderedByPriceDesc();

    ProductionPlan plan = new ProductionPlan();

    for (Product product : products) {
      List<ProductRawMaterial> recipe = productRawMaterialRepository.listByProduct(product.getId());

      if (recipe.isEmpty()) continue;

      BigDecimal maxUnits = computeMaxUnits(recipe, remainingStock);

      if (maxUnits.compareTo(BigDecimal.ZERO) <= 0) continue;

      consumeStock(recipe, remainingStock, maxUnits);
      BigDecimal itemTotalValue = product.getPrice().multiply(maxUnits);

      ProductionCapacity capacity =
          new ProductionCapacity(
              product.getId(),
              product.getCode(),
              product.getName(),
              product.getPrice(),
              maxUnits,
              itemTotalValue);

      plan.getItems().add(capacity);
      plan.setGrandTotalValue(plan.getGrandTotalValue().add(itemTotalValue));
    }

    return plan;
  }

  private Map<Long, BigDecimal> loadStock() {
    Map<Long, BigDecimal> stock = new HashMap<>();
    List<RawMaterial> materials = rawMaterialRepository.listAllRawMaterials();
    for (RawMaterial rm : materials) {
      BigDecimal qty = rm.getStockQuantity() == null ? BigDecimal.ZERO : rm.getStockQuantity();
      stock.put(rm.getId(), qty);
    }
    return stock;
  }

  private BigDecimal computeMaxUnits(
      List<ProductRawMaterial> recipe, Map<Long, BigDecimal> remainingStock) {
    BigDecimal maxUnits = null;

    for (ProductRawMaterial link : recipe) {
      Long rawMaterialId = link.getRawMaterial().getId();
      BigDecimal stock = remainingStock.getOrDefault(rawMaterialId, BigDecimal.ZERO);
      BigDecimal required = link.getRequiredQuantity();

      if (required == null || required.compareTo(BigDecimal.ZERO) <= 0) {
        return BigDecimal.ZERO;
      }

      BigDecimal possibleUnits = stock.divide(required, 0, RoundingMode.DOWN);

      if (maxUnits == null || possibleUnits.compareTo(maxUnits) < 0) {
        maxUnits = possibleUnits;
      }

      if (maxUnits.compareTo(BigDecimal.ZERO) == 0) {
        return BigDecimal.ZERO;
      }
    }

    return maxUnits == null ? BigDecimal.ZERO : maxUnits;
  }

  private void consumeStock(
      List<ProductRawMaterial> recipe, Map<Long, BigDecimal> remainingStock, BigDecimal units) {
    for (ProductRawMaterial link : recipe) {
      Long rawMaterialId = link.getRawMaterial().getId();
      BigDecimal stock = remainingStock.getOrDefault(rawMaterialId, BigDecimal.ZERO);
      BigDecimal totalRequired = link.getRequiredQuantity().multiply(units);
      BigDecimal newStock = stock.subtract(totalRequired);

      if (newStock.compareTo(BigDecimal.ZERO) < 0) {
        newStock = BigDecimal.ZERO;
      }

      remainingStock.put(rawMaterialId, newStock);
    }
  }
}
