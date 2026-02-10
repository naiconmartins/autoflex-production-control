package org.autoflex.factory;

import org.autoflex.domain.entities.Product;
import org.autoflex.domain.entities.ProductRawMaterial;
import org.autoflex.domain.entities.RawMaterial;
import org.autoflex.web.dto.ProductionCapacityDTO;
import org.autoflex.web.dto.ProductionPlanResponseDTO;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ProductionCapacityFactory {

    public static ProductionPlanResponseDTO createEmptyResponse() {
        ProductionPlanResponseDTO response = new ProductionPlanResponseDTO();
        response.items = new ArrayList<>();
        response.grandTotalValue = BigDecimal.ZERO;
        return response;
    }

    public static ProductionCapacityDTO createItemDTO(Product product, BigDecimal quantity) {
        BigDecimal totalValue = product.getPrice().multiply(quantity);
        return new ProductionCapacityDTO(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getPrice(),
                quantity,
                totalValue
        );
    }

    public static ProductRawMaterial createLink(Product product, RawMaterial rawMaterial, String requiredQuantity) {
        return new ProductRawMaterial(product, rawMaterial, new BigDecimal(requiredQuantity));
    }
}