package org.autoflex.application.services;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.autoflex.adapters.inbound.dto.response.ProductionCapacityDTO;
import org.autoflex.adapters.inbound.dto.response.ProductionPlanResponseDTO;
import org.autoflex.domain.Product;
import org.autoflex.domain.ProductRawMaterial;
import org.autoflex.domain.RawMaterial;

@ApplicationScoped
public class ProductionCapacityService {

  public ProductionPlanResponseDTO generate() {
    Map<Long, BigDecimal> remainingStock = loadStock();
    List<Product> products = Product.listAll(Sort.by("price").descending());

    ProductionPlanResponseDTO response = new ProductionPlanResponseDTO();

    for (Product product : products) {
      List<ProductRawMaterial> recipe = ProductRawMaterial.find("product", product).list();

      if (recipe == null || recipe.isEmpty()) {
        continue;
      }

      BigDecimal maxUnits = computeMaxUnits(recipe, remainingStock);

      if (maxUnits.compareTo(BigDecimal.ZERO) <= 0) {
        continue;
      }

      consumeStock(recipe, remainingStock, maxUnits);

      BigDecimal totalValue = product.getPrice().multiply(maxUnits);

      response.items.add(
          new ProductionCapacityDTO(
              product.getId(),
              product.getCode(),
              product.getName(),
              product.getPrice(),
              maxUnits,
              totalValue));

      response.grandTotalValue = response.grandTotalValue.add(totalValue);
    }

    return response;
  }

  private Map<Long, BigDecimal> loadStock() {
    Map<Long, BigDecimal> stock = new HashMap<>();
    List<RawMaterial> materials = RawMaterial.listAll();
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
