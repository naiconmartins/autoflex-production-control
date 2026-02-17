package org.autoflex.adapters.outbound.persistence.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.autoflex.domain.ProductRawMaterial;
import org.autoflex.fixtures.ProductRawMaterialFixture;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductRawMaterialPersistenceAdapterIT {

  @Inject ProductRawMaterialPersistenceAdapter adapter;

  @Test
  @Transactional
  void save_shouldInsertAssociation_whenValidDomain() {
    ProductRawMaterial link =
        ProductRawMaterialFixture.createLink(null, 1L, 18L, new BigDecimal("2.50"));

    ProductRawMaterial saved = adapter.save(link);

    assertNotNull(saved.getId());
    assertEquals(1L, saved.getProduct().getId());
    assertEquals(18L, saved.getRawMaterial().getId());
    assertEquals(0, new BigDecimal("2.50").compareTo(saved.getRequiredQuantity()));
  }

  @Test
  @Transactional
  void save_shouldUpdateAssociation_whenIdExists() {
    ProductRawMaterial created =
        adapter.save(ProductRawMaterialFixture.createLink(null, 2L, 18L, new BigDecimal("1.25")));

    created.setRequiredQuantity(new BigDecimal("3.75"));
    ProductRawMaterial updated = adapter.save(created);

    assertEquals(created.getId(), updated.getId());
    assertEquals(0, new BigDecimal("3.75").compareTo(updated.getRequiredQuantity()));

    Optional<ProductRawMaterial> persisted = adapter.findById(updated.getId());
    assertTrue(persisted.isPresent());
    assertEquals(0, new BigDecimal("3.75").compareTo(persisted.get().getRequiredQuantity()));
  }

  @Test
  void findByProductAndRawMaterial_shouldReturnAssociation_whenPairExists() {
    Optional<ProductRawMaterial> result = adapter.findByProductAndRawMaterial(1L, 2L);

    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getProduct().getId());
    assertEquals(2L, result.get().getRawMaterial().getId());
    assertEquals(0, new BigDecimal("8.00").compareTo(result.get().getRequiredQuantity()));
  }

  @Test
  void findByProductAndRawMaterial_shouldReturnEmpty_whenPairDoesNotExist() {
    Optional<ProductRawMaterial> result = adapter.findByProductAndRawMaterial(1L, 19L);

    assertTrue(result.isEmpty());
  }

  @Test
  void findById_shouldReturnAssociation_whenIdExists() {
    Optional<ProductRawMaterial> existing = adapter.findByProductAndRawMaterial(1L, 2L);
    assertTrue(existing.isPresent());

    Optional<ProductRawMaterial> result = adapter.findById(existing.get().getId());

    assertTrue(result.isPresent());
    assertEquals(existing.get().getId(), result.get().getId());
  }

  @Test
  void findById_shouldReturnEmpty_whenIdDoesNotExist() {
    Optional<ProductRawMaterial> result = adapter.findById(999_999L);

    assertTrue(result.isEmpty());
  }

  @Test
  void listByProduct_shouldReturnAssociations_whenProductHasRawMaterials() {
    List<ProductRawMaterial> result = adapter.listByProduct(1L);

    assertFalse(result.isEmpty());
    assertTrue(result.stream().allMatch(item -> item.getProduct().getId().equals(1L)));
    assertTrue(result.stream().anyMatch(item -> item.getRawMaterial().getId().equals(2L)));
  }

  @Test
  void listByProduct_shouldReturnEmptyList_whenProductHasNoRawMaterials() {
    List<ProductRawMaterial> result = adapter.listByProduct(999_999L);

    assertTrue(result.isEmpty());
  }

  @Test
  @Transactional
  void delete_shouldDeleteAssociation_whenIdExists() {
    ProductRawMaterial created =
        adapter.save(ProductRawMaterialFixture.createLink(null, 3L, 18L, new BigDecimal("0.80")));

    adapter.delete(created.getId());

    Optional<ProductRawMaterial> result = adapter.findById(created.getId());
    assertTrue(result.isEmpty());
  }

  @Test
  @Transactional
  void delete_shouldNotFail_whenIdDoesNotExist() {
    adapter.delete(999_999L);
  }

  @Test
  @Transactional
  void save_shouldFail_whenProductDoesNotExist() {
    ProductRawMaterial invalid =
        ProductRawMaterialFixture.createLink(null, 999_999L, 1L, new BigDecimal("1.00"));

    assertThrows(PersistenceException.class, () -> adapter.save(invalid));
  }

  @Test
  @Transactional
  void save_shouldFail_whenRawMaterialDoesNotExist() {
    ProductRawMaterial invalid =
        ProductRawMaterialFixture.createLink(null, 1L, 999_999L, new BigDecimal("1.00"));

    assertThrows(PersistenceException.class, () -> adapter.save(invalid));
  }

  @Test
  @Transactional
  void save_shouldFail_whenAssociationAlreadyExists() {
    ProductRawMaterial duplicate =
        ProductRawMaterialFixture.createLink(null, 1L, 2L, new BigDecimal("99.99"));

    assertThrows(PersistenceException.class, () -> adapter.save(duplicate));
  }
}
