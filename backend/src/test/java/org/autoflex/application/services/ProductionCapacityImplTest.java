package org.autoflex.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.autoflex.application.gateways.ProductRawMaterialRepository;
import org.autoflex.application.gateways.ProductRepository;
import org.autoflex.application.gateways.RawMaterialRepository;
import org.autoflex.domain.Product;
import org.autoflex.domain.ProductRawMaterial;
import org.autoflex.domain.ProductionPlan;
import org.autoflex.domain.RawMaterial;
import org.autoflex.fixtures.ProductRawMaterialFixture;
import org.autoflex.fixtures.RawMaterialFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductionCapacityImplTest {

  @Mock ProductRepository productRepository;
  @Mock RawMaterialRepository rawMaterialRepository;
  @Mock ProductRawMaterialRepository productRawMaterialRepository;

  @InjectMocks ProductionCapacityImpl productionCapacity;

  @Test
  void generate_shouldReturnEmptyPlan_whenNoProducts() {
    when(rawMaterialRepository.listAllRawMaterials()).thenReturn(List.of());
    when(productRepository.findAllOrderedByPriceDesc()).thenReturn(List.of());

    ProductionPlan result = productionCapacity.generate();

    assertNotNull(result);
    assertTrue(result.getItems().isEmpty());
    assertEquals(0, BigDecimal.ZERO.compareTo(result.getGrandTotalValue()));
    verify(productRawMaterialRepository, never())
        .listByProduct(org.mockito.ArgumentMatchers.anyLong());
  }

  @Test
  void generate_shouldSkipProduct_whenRecipeIsEmpty() {
    Product product = createProduct(1L, "PROD-001", "Table", "100.00");

    when(rawMaterialRepository.listAllRawMaterials())
        .thenReturn(List.of(createRawMaterial(1L, "100.00")));
    when(productRepository.findAllOrderedByPriceDesc()).thenReturn(List.of(product));
    when(productRawMaterialRepository.listByProduct(1L)).thenReturn(List.of());

    ProductionPlan result = productionCapacity.generate();

    assertNotNull(result);
    assertTrue(result.getItems().isEmpty());
    assertEquals(0, BigDecimal.ZERO.compareTo(result.getGrandTotalValue()));
  }

  @Test
  void generate_shouldComputeUsingBottleneckAndRoundDown_whenRecipeIsValid() {
    Product product = createProduct(1L, "PROD-001", "Table", "100.00");

    RawMaterial rm1 = createRawMaterial(1L, "10.00");
    RawMaterial rm2 = createRawMaterial(2L, "9.00");

    List<ProductRawMaterial> recipe =
        List.of(createLink(1L, product, rm1, "2.00"), createLink(2L, product, rm2, "4.00"));

    when(rawMaterialRepository.listAllRawMaterials()).thenReturn(List.of(rm1, rm2));
    when(productRepository.findAllOrderedByPriceDesc()).thenReturn(List.of(product));
    when(productRawMaterialRepository.listByProduct(1L)).thenReturn(recipe);

    ProductionPlan result = productionCapacity.generate();

    assertEquals(1, result.getItems().size());
    assertEquals(
        0, new BigDecimal("2").compareTo(result.getItems().getFirst().getProducibleQuantity()));
    assertEquals(
        0, new BigDecimal("200.00").compareTo(result.getItems().getFirst().getTotalValue()));
    assertEquals(0, new BigDecimal("200.00").compareTo(result.getGrandTotalValue()));
  }

  @Test
  void generate_shouldSkipProduct_whenRequiredQuantityIsInvalid() {
    Product product = createProduct(1L, "PROD-001", "Table", "100.00");
    RawMaterial rm = createRawMaterial(1L, "100.00");

    List<ProductRawMaterial> recipe = List.of(createLink(1L, product, rm, "0.00"));

    when(rawMaterialRepository.listAllRawMaterials()).thenReturn(List.of(rm));
    when(productRepository.findAllOrderedByPriceDesc()).thenReturn(List.of(product));
    when(productRawMaterialRepository.listByProduct(1L)).thenReturn(recipe);

    ProductionPlan result = productionCapacity.generate();

    assertTrue(result.getItems().isEmpty());
    assertEquals(0, BigDecimal.ZERO.compareTo(result.getGrandTotalValue()));
  }

  @Test
  void generate_shouldConsumeStockAcrossProducts_whenFirstProductUsesSharedMaterial() {
    Product expensive = createProduct(1L, "PROD-A", "Expensive", "100.00");
    Product cheaper = createProduct(2L, "PROD-B", "Cheaper", "50.00");

    RawMaterial shared = createRawMaterial(1L, "10.00");

    List<ProductRawMaterial> recipeA = List.of(createLink(1L, expensive, shared, "4.00"));
    List<ProductRawMaterial> recipeB = List.of(createLink(2L, cheaper, shared, "2.00"));

    when(rawMaterialRepository.listAllRawMaterials()).thenReturn(List.of(shared));
    when(productRepository.findAllOrderedByPriceDesc()).thenReturn(List.of(expensive, cheaper));
    when(productRawMaterialRepository.listByProduct(1L)).thenReturn(recipeA);
    when(productRawMaterialRepository.listByProduct(2L)).thenReturn(recipeB);

    ProductionPlan result = productionCapacity.generate();

    assertEquals(2, result.getItems().size());
    assertEquals(
        0, new BigDecimal("2").compareTo(result.getItems().get(0).getProducibleQuantity()));
    assertEquals(
        0, new BigDecimal("1").compareTo(result.getItems().get(1).getProducibleQuantity()));
    assertEquals(0, new BigDecimal("250.00").compareTo(result.getGrandTotalValue()));
  }

  @Test
  void generate_shouldTreatNullStockAsZero_whenRawMaterialHasNullQuantity() {
    Product product = createProduct(1L, "PROD-001", "Table", "100.00");
    RawMaterial rm = createRawMaterial(1L, null);

    List<ProductRawMaterial> recipe = List.of(createLink(1L, product, rm, "1.00"));

    when(rawMaterialRepository.listAllRawMaterials()).thenReturn(List.of(rm));
    when(productRepository.findAllOrderedByPriceDesc()).thenReturn(List.of(product));
    when(productRawMaterialRepository.listByProduct(1L)).thenReturn(recipe);

    ProductionPlan result = productionCapacity.generate();

    assertTrue(result.getItems().isEmpty());
    assertEquals(0, BigDecimal.ZERO.compareTo(result.getGrandTotalValue()));
  }

  @Test
  void generate_shouldTreatMissingStockAsZero_whenRecipeRawMaterialNotInStockMap() {
    Product product = createProduct(1L, "PROD-001", "Table", "100.00");

    RawMaterial stockOnlyRm = createRawMaterial(1L, "10.00");
    RawMaterial recipeRm = createRawMaterial(2L, "999.00");

    List<ProductRawMaterial> recipe = List.of(createLink(1L, product, recipeRm, "1.00"));

    when(rawMaterialRepository.listAllRawMaterials()).thenReturn(List.of(stockOnlyRm));
    when(productRepository.findAllOrderedByPriceDesc()).thenReturn(List.of(product));
    when(productRawMaterialRepository.listByProduct(1L)).thenReturn(recipe);

    ProductionPlan result = productionCapacity.generate();

    assertTrue(result.getItems().isEmpty());
    assertEquals(0, BigDecimal.ZERO.compareTo(result.getGrandTotalValue()));
  }

  @Test
  void generate_shouldSkipProduct_whenRequiredQuantityIsNull() {
    Product product = createProduct(1L, "PROD-001", "Table", "100.00");
    RawMaterial rm = createRawMaterial(1L, "100.00");

    ProductRawMaterial link =
        ProductRawMaterialFixture.createLink(1L, product.getId(), rm.getId(), BigDecimal.ONE);
    link.setProduct(product);
    link.setRawMaterial(rm);
    link.setRequiredQuantity(null);

    when(rawMaterialRepository.listAllRawMaterials()).thenReturn(List.of(rm));
    when(productRepository.findAllOrderedByPriceDesc()).thenReturn(List.of(product));
    when(productRawMaterialRepository.listByProduct(1L)).thenReturn(List.of(link));

    ProductionPlan result = productionCapacity.generate();

    assertTrue(result.getItems().isEmpty());
    assertEquals(0, BigDecimal.ZERO.compareTo(result.getGrandTotalValue()));
  }

  @Test
  void generate_shouldKeepOnlyProducibleProducts_whenMixedProductSet() {
    Product highPrice = createProduct(1L, "PROD-A", "High", "500.00");
    Product mediumPrice = createProduct(2L, "PROD-B", "Medium", "300.00");
    Product lowPrice = createProduct(3L, "PROD-C", "Low", "100.00");

    RawMaterial rmA = createRawMaterial(1L, "14.00");
    RawMaterial rmB = createRawMaterial(2L, "2.00");

    List<ProductRawMaterial> recipeA = List.of(createLink(1L, highPrice, rmA, "5.00"));
    List<ProductRawMaterial> recipeB = List.of(createLink(2L, mediumPrice, rmB, "3.00"));
    List<ProductRawMaterial> recipeC = List.of(createLink(3L, lowPrice, rmA, "2.00"));

    when(rawMaterialRepository.listAllRawMaterials()).thenReturn(List.of(rmA, rmB));
    when(productRepository.findAllOrderedByPriceDesc())
        .thenReturn(List.of(highPrice, mediumPrice, lowPrice));
    when(productRawMaterialRepository.listByProduct(1L)).thenReturn(recipeA);
    when(productRawMaterialRepository.listByProduct(2L)).thenReturn(recipeB);
    when(productRawMaterialRepository.listByProduct(3L)).thenReturn(recipeC);

    ProductionPlan result = productionCapacity.generate();

    assertEquals(2, result.getItems().size());
    assertEquals("PROD-A", result.getItems().get(0).getProductCode());
    assertEquals("PROD-C", result.getItems().get(1).getProductCode());
  }

  @Test
  void generate_shouldAccumulateGrandTotalAcrossThreeProducts() {
    Product p1 = createProduct(1L, "PROD-A", "A", "100.00");
    Product p2 = createProduct(2L, "PROD-B", "B", "80.00");
    Product p3 = createProduct(3L, "PROD-C", "C", "60.00");

    RawMaterial rm1 = createRawMaterial(1L, "14.00");
    RawMaterial rm2 = createRawMaterial(2L, "3.00");
    RawMaterial rm3 = createRawMaterial(3L, "2.00");

    when(rawMaterialRepository.listAllRawMaterials()).thenReturn(List.of(rm1, rm2, rm3));
    when(productRepository.findAllOrderedByPriceDesc()).thenReturn(List.of(p1, p2, p3));
    when(productRawMaterialRepository.listByProduct(1L))
        .thenReturn(List.of(createLink(1L, p1, rm1, "7.00")));
    when(productRawMaterialRepository.listByProduct(2L))
        .thenReturn(List.of(createLink(2L, p2, rm2, "3.00")));
    when(productRawMaterialRepository.listByProduct(3L))
        .thenReturn(List.of(createLink(3L, p3, rm3, "2.00")));

    ProductionPlan result = productionCapacity.generate();

    assertEquals(3, result.getItems().size());
    assertEquals(0, new BigDecimal("340.00").compareTo(result.getGrandTotalValue()));
  }

  private static Product createProduct(Long id, String code, String name, String price) {
    Product product = ProductRawMaterialFixture.createProduct(id);
    product.setCode(code);
    product.setName(name);
    product.setPrice(new BigDecimal(price));
    return product;
  }

  private static RawMaterial createRawMaterial(Long id, String stockQuantity) {
    RawMaterial rm = RawMaterialFixture.createRawMaterial(id);
    if (stockQuantity == null) {
      rm.setStockQuantity(null);
    } else {
      rm.setStockQuantity(new BigDecimal(stockQuantity));
    }
    return rm;
  }

  private static ProductRawMaterial createLink(
      Long id, Product product, RawMaterial rawMaterial, String requiredQuantity) {
    ProductRawMaterial link =
        ProductRawMaterialFixture.createLink(
            id, product.getId(), rawMaterial.getId(), new BigDecimal(requiredQuantity));
    link.setProduct(product);
    link.setRawMaterial(rawMaterial);
    return link;
  }
}
