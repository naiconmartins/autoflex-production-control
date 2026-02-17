package org.autoflex.adapters.outbound.persistence.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.application.dto.SearchQuery;
import org.autoflex.domain.Product;
import org.autoflex.domain.RawMaterial;
import org.autoflex.fixtures.ProductFixture;
import org.autoflex.fixtures.RawMaterialFixture;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductPersistenceAdapterIT {

  @Inject ProductPersistenceAdapter adapter;

  @Test
  @Transactional
  void save_shouldInsertProduct_whenValidDomain() {
    Product product = createNewProduct("IT Product", "99.90");

    Product saved = adapter.save(product);

    assertNotNull(saved.getId());
    assertEquals(product.getCode(), saved.getCode());
    assertEquals(product.getName(), saved.getName());
    assertEquals(0, product.getPrice().compareTo(saved.getPrice()));
  }

  @Test
  @Transactional
  void save_shouldUpdateProduct_whenIdExists() {
    Product created = adapter.save(createNewProduct("Before Update", "100.00"));

    created.setCode(uniqueCode());
    created.setName("After Update");
    created.setPrice(new BigDecimal("150.50"));

    Product updated = adapter.save(created);

    assertEquals(created.getId(), updated.getId());
    assertEquals("After Update", updated.getName());
    assertEquals(0, new BigDecimal("150.50").compareTo(updated.getPrice()));

    Optional<Product> persisted = adapter.findById(created.getId());
    assertTrue(persisted.isPresent());
    assertEquals("After Update", persisted.get().getName());
  }

  @Test
  @Transactional
  void save_shouldInsertProductWithRawMaterials_whenDomainContainsAssociations() {
    Product product = createNewProduct("Product With Recipe", "220.00");
    RawMaterial rm1 = RawMaterialFixture.createRawMaterial(1L);
    RawMaterial rm2 = RawMaterialFixture.createRawMaterial(2L);
    product.addRawMaterial(rm1, new BigDecimal("2.00"));
    product.addRawMaterial(rm2, new BigDecimal("4.00"));

    Product saved = adapter.save(product);

    assertNotNull(saved.getId());
    assertEquals(2, saved.getRawMaterials().size());
    assertTrue(
        saved.getRawMaterials().stream().anyMatch(i -> i.getRawMaterial().getId().equals(1L)));
    assertTrue(
        saved.getRawMaterials().stream().anyMatch(i -> i.getRawMaterial().getId().equals(2L)));
  }

  @Test
  @Transactional
  void save_shouldReplaceRawMaterials_whenUpdatingExistingProductRecipe() {
    Product created = createNewProduct("Recipe Replace", "300.00");
    created.addRawMaterial(RawMaterialFixture.createRawMaterial(1L), new BigDecimal("2.00"));
    Product persisted = adapter.save(created);

    persisted.clearRawMaterials();
    persisted.addRawMaterial(RawMaterialFixture.createRawMaterial(2L), new BigDecimal("6.00"));

    Product updated = adapter.save(persisted);

    assertEquals(1, updated.getRawMaterials().size());
    assertEquals(2L, updated.getRawMaterials().getFirst().getRawMaterial().getId());
    assertEquals(
        0,
        new BigDecimal("6.00")
            .compareTo(updated.getRawMaterials().getFirst().getRequiredQuantity()));
  }

  @Test
  void findByCode_shouldReturnProduct_whenCodeExists() {
    Optional<Product> result = adapter.findByCode("PROD-001");

    assertTrue(result.isPresent());
    assertEquals("PROD-001", result.get().getCode());
  }

  @Test
  void findByCode_shouldReturnEmpty_whenCodeDoesNotExist() {
    Optional<Product> result = adapter.findByCode("NOT-FOUND-" + UUID.randomUUID());

    assertTrue(result.isEmpty());
  }

  @Test
  void findById_shouldReturnProduct_whenIdExists() {
    Optional<Product> result = adapter.findById(1L);

    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId());
  }

  @Test
  void findById_shouldReturnEmpty_whenIdDoesNotExist() {
    Optional<Product> result = adapter.findById(999_999L);

    assertTrue(result.isEmpty());
  }

  @Test
  @Transactional
  void delete_shouldDeleteProduct_whenIdExists() {
    Product created = adapter.save(createNewProduct("To Delete", "33.33"));

    adapter.delete(created.getId());

    Optional<Product> result = adapter.findById(created.getId());
    assertTrue(result.isEmpty());
  }

  @Test
  @Transactional
  void delete_shouldNotFail_whenIdDoesNotExist() {
    adapter.delete(999_999L);
  }

  @Test
  void findAll_shouldReturnPagedProducts_whenValidQuery() {
    SearchQuery query = new SearchQuery(0, 5, "name", "asc");

    PagedModel<Product> result = adapter.findAll(query);

    assertNotNull(result);
    assertTrue(result.totalElements() > 0);
    assertTrue(result.totalPages() > 0);
    assertTrue(result.items().size() <= 5);
  }

  @Test
  void findAll_shouldReturnEmptyItems_whenPageIsOutOfRange() {
    SearchQuery query = new SearchQuery(999, 10, "name", "asc");

    PagedModel<Product> result = adapter.findAll(query);

    assertNotNull(result);
    assertTrue(result.items().isEmpty());
  }

  @Test
  void findByName_shouldReturnProducts_whenNameMatches() {
    SearchQuery query = new SearchQuery(0, 10, "name", "asc");

    PagedModel<Product> result = adapter.findByName("Dining", query);

    assertNotNull(result);
    assertTrue(result.totalElements() > 0);
    assertFalse(result.items().isEmpty());
    assertTrue(result.items().stream().anyMatch(p -> p.getName().contains("Dining")));
  }

  @Test
  void findByName_shouldReturnEmptyPage_whenNoMatch() {
    SearchQuery query = new SearchQuery(0, 10, "name", "asc");

    PagedModel<Product> result = adapter.findByName("NOT-FOUND-" + UUID.randomUUID(), query);

    assertNotNull(result);
    assertEquals(0L, result.totalElements());
    assertTrue(result.items().isEmpty());
  }

  @Test
  void findAllOrderedByPriceDesc_shouldReturnProductsSortedByPriceDesc() {
    List<Product> result = adapter.findAllOrderedByPriceDesc();

    assertFalse(result.isEmpty());
    if (result.size() > 1) {
      assertTrue(result.get(0).getPrice().compareTo(result.get(1).getPrice()) >= 0);
    }
  }

  private static String uniqueCode() {
    return "IT-PERSIST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }

  private static Product createNewProduct(String name, String price) {
    Product product = ProductFixture.createProduct();
    product.setCode(uniqueCode());
    product.setName(name);
    product.setPrice(new BigDecimal(price));
    return product;
  }
}
