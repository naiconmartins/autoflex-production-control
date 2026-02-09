package org.autoflex.application.services;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.autoflex.domain.entities.Product;
import org.autoflex.domain.entities.ProductRawMaterial;
import org.autoflex.domain.entities.RawMaterial;
import org.autoflex.web.dto.ProductionPlanItemDTO;
import org.autoflex.web.dto.ProductionPlanResponseDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ProductionPlanService {

    public ProductionPlanResponseDTO generate() {
        Map<Long, BigDecimal> remainingStock = loadStock();
        List<Product> products = Product.listAll(Sort.by("price").descending());

        ProductionPlanResponseDTO response = new ProductionPlanResponseDTO();

        for (Product product : products) {
            List<ProductRawMaterial> recipe = ProductRawMaterial.find("product", product).list();
            if (recipe == null || recipe.isEmpty()) {
                continue;
            }

            long maxUnits = computeMaxUnits(recipe, remainingStock);
            if (maxUnits <= 0) {
                continue;
            }

            consumeStock(recipe, remainingStock, maxUnits);

            BigDecimal totalValue = product.getPrice().multiply(BigDecimal.valueOf(maxUnits));
            response.items.add(new ProductionPlanItemDTO(
                    product.getId(),
                    product.getCode(),
                    product.getName(),
                    product.getPrice(),
                    maxUnits,
                    totalValue
            ));
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

    private long computeMaxUnits(List<ProductRawMaterial> recipe, Map<Long, BigDecimal> remainingStock) {
        long maxUnits = Long.MAX_VALUE;

        for (ProductRawMaterial link : recipe) {
            Long rawMaterialId = link.getRawMaterial().getId();
            BigDecimal stock = remainingStock.getOrDefault(rawMaterialId, BigDecimal.ZERO);
            BigDecimal required = link.getRequiredQuantity();

            if (required == null || required.compareTo(BigDecimal.ZERO) <= 0) {
                return 0L;
            }

            long units = floorDivide(stock, required);
            if (units < maxUnits) {
                maxUnits = units;
            }

            if (maxUnits == 0L) {
                return 0L;
            }
        }

        return maxUnits == Long.MAX_VALUE ? 0L : maxUnits;
    }

    private void consumeStock(List<ProductRawMaterial> recipe, Map<Long, BigDecimal> remainingStock, long units) {
        BigDecimal unitsBd = BigDecimal.valueOf(units);

        for (ProductRawMaterial link : recipe) {
            Long rawMaterialId = link.getRawMaterial().getId();
            BigDecimal stock = remainingStock.getOrDefault(rawMaterialId, BigDecimal.ZERO);
            BigDecimal required = link.getRequiredQuantity().multiply(unitsBd);
            BigDecimal newStock = stock.subtract(required);
            if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                newStock = BigDecimal.ZERO;
            }
            remainingStock.put(rawMaterialId, newStock);
        }
    }

    private long floorDivide(BigDecimal dividend, BigDecimal divisor) {
        if (dividend == null || divisor == null || divisor.compareTo(BigDecimal.ZERO) <= 0) {
            return 0L;
        }
        if (dividend.compareTo(BigDecimal.ZERO) <= 0) {
            return 0L;
        }
        return dividend.divide(divisor, 0, RoundingMode.DOWN).longValue();
    }
}