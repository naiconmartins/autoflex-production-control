package org.autoflex.factory;

import org.autoflex.domain.entities.Product;
import org.autoflex.domain.entities.ProductRawMaterial;
import org.autoflex.domain.entities.RawMaterial;
import org.autoflex.web.dto.ProductRawMaterialRequestDTO;

import java.math.BigDecimal;
import java.util.List;

public class ProductRawMaterialFactory {

    public static Product createProduct(Long id, String code) {
        Product product = new Product(code, "Product " + code, new BigDecimal("100.00"));
        product.setId(id);
        return product;
    }

    public static RawMaterial createRawMaterial(Long id, String code) {
        RawMaterial rawMaterial = new RawMaterial(code, "Raw " + code, new BigDecimal("100.00"));
        rawMaterial.setId(id);
        return rawMaterial;
    }

    public static ProductRawMaterial createLink(Product product, RawMaterial rawMaterial, BigDecimal requiredQuantity) {
        ProductRawMaterial link = new ProductRawMaterial(product, rawMaterial, requiredQuantity);
        link.setId(1L);
        return link;
    }

    public static ProductRawMaterialRequestDTO createRequest(Long rawMaterialId, BigDecimal requiredQuantity) {
        return new ProductRawMaterialRequestDTO(rawMaterialId, requiredQuantity);
    }

    public static List<ProductRawMaterialRequestDTO> createListOfRawMaterialsRequestDTO() {
        return List.of(
                new ProductRawMaterialRequestDTO(1L, new BigDecimal("10.00")),
                new ProductRawMaterialRequestDTO(2L, new BigDecimal("5.00"))
        );
    }
}
