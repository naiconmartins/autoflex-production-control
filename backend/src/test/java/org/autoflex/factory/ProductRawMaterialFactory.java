package org.autoflex.factory;

import org.autoflex.domain.entities.Product;
import org.autoflex.domain.entities.ProductRawMaterial;
import org.autoflex.domain.entities.RawMaterial;
import org.autoflex.web.dto.ProductRawMaterialRequestDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductRawMaterialFactory {

    public static List<ProductRawMaterial> createListOfRawMaterials() {
        Product product = ProductFactory.createProduct();

        RawMaterial rawMaterial1 = RawMaterialFactory.createCustomRawMaterial(1L);
        RawMaterial rawMaterial2 = RawMaterialFactory.createCustomRawMaterial(2L);

        ProductRawMaterial productRawMaterial1 = new ProductRawMaterial(product, rawMaterial1, new BigDecimal("10.0"));
        ProductRawMaterial productRawMaterial2 = new ProductRawMaterial(product, rawMaterial2, new BigDecimal("10.0"));

        productRawMaterial1.setId(1L);
        productRawMaterial2.setId(2L);

        List<ProductRawMaterial> list = new ArrayList<>();
        list.add(productRawMaterial1);
        list.add(productRawMaterial2);

        return list;
    }

    public static List<ProductRawMaterialRequestDTO> createListOfRawMaterialsRequestDTO() {

        List<ProductRawMaterialRequestDTO> list = new ArrayList<>();
        ProductRawMaterialRequestDTO dto1 = new ProductRawMaterialRequestDTO(1L, new BigDecimal("10.00"));
        ProductRawMaterialRequestDTO dto2 = new ProductRawMaterialRequestDTO(2L, new BigDecimal("5.00"));
        list.add(dto1);
        list.add(dto2);

        return list;
    }
}
