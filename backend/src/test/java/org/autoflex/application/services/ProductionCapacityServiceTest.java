package org.autoflex.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import org.autoflex.adapters.inbound.dto.response.ProductionPlanResponseDTO;
import org.autoflex.domain.Product;
import org.autoflex.domain.ProductRawMaterial;
import org.autoflex.domain.RawMaterial;
import org.autoflex.factory.ProductFactory;
import org.autoflex.factory.ProductionCapacityFactory;
import org.autoflex.factory.RawMaterialFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductionCapacityServiceTest {

  @Inject ProductionCapacityService service;

  @InjectMock EntityManager entityManager;

  private RawMaterial wood;
  private Product table;
  private Product chair;

  @BeforeEach
  void setup() {
    PanacheMock.mock(Product.class);
    PanacheMock.mock(RawMaterial.class);
    PanacheMock.mock(ProductRawMaterial.class);

    when(Product.getEntityManager()).thenReturn(entityManager);
    when(RawMaterial.getEntityManager()).thenReturn(entityManager);
    when(ProductRawMaterial.getEntityManager()).thenReturn(entityManager);

    wood = RawMaterialFactory.createRawMaterialWithCode("WOOD");
    wood.setId(1L);
    wood.setStockQuantity(new BigDecimal("100.0"));

    table = ProductFactory.createProductWithCode("TAB01");
    table.setId(10L);

    chair = ProductFactory.createProductWithCode("CHA01");
    chair.setId(11L);
  }

  private void mockRecipe(Product product, List<ProductRawMaterial> recipe) {
    PanacheQuery query = mock(PanacheQuery.class);
    when(query.list()).thenReturn(recipe);
    when(ProductRawMaterial.find("product", product)).thenReturn(query);
  }

  @Test
  void generate_shouldConsumeStockInPriceOrder_andSkipWhenNoStockRemains() {
    when(RawMaterial.listAll()).thenReturn(List.of(wood));
    when(Product.listAll(any(Sort.class))).thenReturn(List.of(table, chair));

    mockRecipe(table, List.of(ProductionCapacityFactory.createLink(table, wood, "5.0")));
    mockRecipe(chair, List.of(ProductionCapacityFactory.createLink(chair, wood, "2.0")));

    ProductionPlanResponseDTO result = service.generate();

    assertNotNull(result);
    assertEquals(1, result.items.size());
    assertEquals(new BigDecimal("20"), result.items.getFirst().producibleQuantity);
  }

  @Test
  void generate_shouldHandleRestrictiveStock() {
    RawMaterial iron = RawMaterialFactory.createRawMaterialWithCode("IRON");
    iron.setId(2L);
    iron.setStockQuantity(new BigDecimal("10.0"));

    when(RawMaterial.listAll()).thenReturn(List.of(wood, iron));
    when(Product.listAll(any(Sort.class))).thenReturn(List.of(table));

    mockRecipe(
        table,
        List.of(
            ProductionCapacityFactory.createLink(table, wood, "1.0"),
            ProductionCapacityFactory.createLink(table, iron, "5.0")));

    ProductionPlanResponseDTO result = service.generate();

    assertEquals(1, result.items.size());
    assertEquals(new BigDecimal("2"), result.items.getFirst().producibleQuantity);
  }

  @Test
  void generate_shouldReturnEmptyItems_whenStockIsZero() {
    wood.setStockQuantity(BigDecimal.ZERO);
    when(RawMaterial.listAll()).thenReturn(List.of(wood));
    when(Product.listAll(any(Sort.class))).thenReturn(List.of(table));

    mockRecipe(table, List.of(ProductionCapacityFactory.createLink(table, wood, "1.0")));

    ProductionPlanResponseDTO result = service.generate();

    assertTrue(result.items.isEmpty());
  }

  @Test
  void generate_shouldHandleProductsWithoutRecipe() {
    when(RawMaterial.listAll()).thenReturn(List.of(wood));
    when(Product.listAll(any(Sort.class))).thenReturn(List.of(table));

    mockRecipe(table, List.of());

    ProductionPlanResponseDTO result = service.generate();

    assertTrue(result.items.isEmpty());
  }

  @Test
  void generate_shouldSkipProduct_whenRecipeIsNull() {
    when(RawMaterial.listAll()).thenReturn(List.of(wood));
    when(Product.listAll(any(Sort.class))).thenReturn(List.of(table));

    mockRecipe(table, null);

    ProductionPlanResponseDTO result = service.generate();

    assertTrue(result.items.isEmpty());
    assertEquals(BigDecimal.ZERO, result.grandTotalValue);
  }

  @Test
  void generate_shouldTreatNullStockAsZero() {
    wood.setStockQuantity(null);
    when(RawMaterial.listAll()).thenReturn(List.of(wood));
    when(Product.listAll(any(Sort.class))).thenReturn(List.of(table));

    mockRecipe(table, List.of(ProductionCapacityFactory.createLink(table, wood, "1.0")));

    ProductionPlanResponseDTO result = service.generate();

    assertTrue(result.items.isEmpty());
    assertEquals(BigDecimal.ZERO, result.grandTotalValue);
  }

  @Test
  void generate_shouldSkipProduct_whenRequiredQuantityIsNull() {
    when(RawMaterial.listAll()).thenReturn(List.of(wood));
    when(Product.listAll(any(Sort.class))).thenReturn(List.of(table));

    ProductRawMaterial link = ProductionCapacityFactory.createLink(table, wood, "1.0");
    link.setRequiredQuantity(null);
    mockRecipe(table, List.of(link));

    ProductionPlanResponseDTO result = service.generate();

    assertTrue(result.items.isEmpty());
    assertEquals(BigDecimal.ZERO, result.grandTotalValue);
  }

  @Test
  void generate_shouldSkipProduct_whenRequiredQuantityIsZeroOrNegative() {
    when(RawMaterial.listAll()).thenReturn(List.of(wood));
    when(Product.listAll(any(Sort.class))).thenReturn(List.of(table));

    mockRecipe(table, List.of(ProductionCapacityFactory.createLink(table, wood, "0.0")));

    ProductionPlanResponseDTO result = service.generate();

    assertTrue(result.items.isEmpty());
    assertEquals(BigDecimal.ZERO, result.grandTotalValue);
  }

  @Test
  void generate_shouldProduceMultipleItems_whenStockRemainsAfterFirstProduct() {
    table.setPrice(new BigDecimal("200.00"));
    chair.setPrice(new BigDecimal("100.00"));

    when(RawMaterial.listAll()).thenReturn(List.of(wood));
    when(Product.listAll(any(Sort.class))).thenReturn(List.of(table, chair));

    mockRecipe(table, List.of(ProductionCapacityFactory.createLink(table, wood, "30.0")));
    mockRecipe(chair, List.of(ProductionCapacityFactory.createLink(chair, wood, "2.0")));

    ProductionPlanResponseDTO result = service.generate();

    assertEquals(2, result.items.size());
    assertEquals(new BigDecimal("3"), result.items.get(0).producibleQuantity);
    assertEquals(new BigDecimal("5"), result.items.get(1).producibleQuantity);
    assertEquals(new BigDecimal("1100.00"), result.grandTotalValue);
  }

  @Test
  void generate_shouldReturnEmptyPlan_whenNoProductsExist() {
    when(RawMaterial.listAll()).thenReturn(List.of(wood));
    when(Product.listAll(any(Sort.class))).thenReturn(List.of());

    ProductionPlanResponseDTO result = service.generate();

    assertNotNull(result);
    assertTrue(result.items.isEmpty());
    assertEquals(BigDecimal.ZERO, result.grandTotalValue);
  }

  @Test
  void generate_shouldReturnEmptyPlan_whenNoRawMaterialsExist() {
    when(RawMaterial.listAll()).thenReturn(List.of());
    when(Product.listAll(any(Sort.class))).thenReturn(List.of(table));
    mockRecipe(table, List.of(ProductionCapacityFactory.createLink(table, wood, "1.0")));

    ProductionPlanResponseDTO result = service.generate();

    assertNotNull(result);
    assertTrue(result.items.isEmpty());
    assertEquals(BigDecimal.ZERO, result.grandTotalValue);
  }

  @Test
  void generate_shouldSkipProduct_whenRecipeUsesMaterialNotPresentInStockMap() {
    RawMaterial ghostRawMaterial = RawMaterialFactory.createRawMaterialWithCode("GHOST");
    ghostRawMaterial.setId(999L);

    when(RawMaterial.listAll()).thenReturn(List.of(wood));
    when(Product.listAll(any(Sort.class))).thenReturn(List.of(table));
    mockRecipe(
        table, List.of(ProductionCapacityFactory.createLink(table, ghostRawMaterial, "1.0")));

    ProductionPlanResponseDTO result = service.generate();

    assertNotNull(result);
    assertTrue(result.items.isEmpty());
    assertEquals(BigDecimal.ZERO, result.grandTotalValue);
  }
}
