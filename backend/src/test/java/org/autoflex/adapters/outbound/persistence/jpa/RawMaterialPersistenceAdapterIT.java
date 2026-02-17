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
import java.util.UUID;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.application.dto.SearchQuery;
import org.autoflex.domain.RawMaterial;
import org.autoflex.fixtures.RawMaterialFixture;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class RawMaterialPersistenceAdapterIT {

  @Inject RawMaterialPersistenceAdapter adapter;

  @Test
  @Transactional
  void save_shouldInsertRawMaterial_whenValidDomain() {
    RawMaterial rawMaterial = createNewRawMaterial("MDF Test Board", "250.00");

    RawMaterial saved = adapter.save(rawMaterial);

    assertNotNull(saved.getId());
    assertEquals(rawMaterial.getCode(), saved.getCode());
    assertEquals(rawMaterial.getName(), saved.getName());
    assertEquals(0, rawMaterial.getStockQuantity().compareTo(saved.getStockQuantity()));
  }

  @Test
  @Transactional
  void save_shouldUpdateRawMaterial_whenIdExists() {
    RawMaterial created = adapter.save(createNewRawMaterial("Before Update", "10.00"));

    created.setCode(uniqueCode());
    created.setName("After Update");
    created.setStockQuantity(new BigDecimal("75.50"));

    RawMaterial updated = adapter.save(created);

    assertEquals(created.getId(), updated.getId());
    assertEquals("After Update", updated.getName());
    assertEquals(0, new BigDecimal("75.50").compareTo(updated.getStockQuantity()));

    Optional<RawMaterial> persisted = adapter.findById(updated.getId());
    assertTrue(persisted.isPresent());
    assertEquals("After Update", persisted.get().getName());
  }

  @Test
  void findByCode_shouldReturnRawMaterial_whenCodeExists() {
    Optional<RawMaterial> result = adapter.findByCode("MAD-001");

    assertTrue(result.isPresent());
    assertEquals("MAD-001", result.get().getCode());
  }

  @Test
  void findByCode_shouldReturnEmpty_whenCodeDoesNotExist() {
    Optional<RawMaterial> result = adapter.findByCode("NOT-FOUND-" + UUID.randomUUID());

    assertTrue(result.isEmpty());
  }

  @Test
  void findById_shouldReturnRawMaterial_whenIdExists() {
    Optional<RawMaterial> result = adapter.findById(1L);

    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId());
  }

  @Test
  void findById_shouldReturnEmpty_whenIdDoesNotExist() {
    Optional<RawMaterial> result = adapter.findById(999_999L);

    assertTrue(result.isEmpty());
  }

  @Test
  @Transactional
  void delete_shouldDeleteRawMaterial_whenIdExists() {
    RawMaterial created = adapter.save(createNewRawMaterial("To Delete", "9.99"));

    adapter.delete(created.getId());

    Optional<RawMaterial> result = adapter.findById(created.getId());
    assertTrue(result.isEmpty());
  }

  @Test
  @Transactional
  void delete_shouldNotFail_whenIdDoesNotExist() {
    adapter.delete(999_999L);
  }

  @Test
  @Transactional
  void delete_shouldFail_whenRawMaterialIsReferencedByProductRawMaterial() {
    assertThrows(PersistenceException.class, () -> adapter.delete(2L));
  }

  @Test
  void findAll_shouldReturnPagedRawMaterials_whenValidQuery() {
    SearchQuery query = new SearchQuery(0, 5, "name", "asc");

    PagedModel<RawMaterial> result = adapter.findAll(query);

    assertNotNull(result);
    assertTrue(result.totalElements() > 0);
    assertTrue(result.totalPages() > 0);
    assertTrue(result.items().size() <= 5);
  }

  @Test
  void findAll_shouldReturnEmptyItems_whenPageIsOutOfRange() {
    SearchQuery query = new SearchQuery(999, 10, "name", "asc");

    PagedModel<RawMaterial> result = adapter.findAll(query);

    assertNotNull(result);
    assertTrue(result.items().isEmpty());
  }

  @Test
  void findByName_shouldReturnRawMaterials_whenNameMatches() {
    SearchQuery query = new SearchQuery(0, 10, "name", "asc");

    PagedModel<RawMaterial> result = adapter.findByName("Wood", query);

    assertNotNull(result);
    assertTrue(result.totalElements() > 0);
    assertFalse(result.items().isEmpty());
    assertTrue(result.items().stream().anyMatch(r -> r.getName().toLowerCase().contains("wood")));
  }

  @Test
  void findByName_shouldReturnEmptyPage_whenNoMatch() {
    SearchQuery query = new SearchQuery(0, 10, "name", "asc");

    PagedModel<RawMaterial> result = adapter.findByName("NOT-FOUND-" + UUID.randomUUID(), query);

    assertNotNull(result);
    assertEquals(0L, result.totalElements());
    assertTrue(result.items().isEmpty());
  }

  @Test
  void listAllRawMaterials_shouldReturnAllItems_whenDatabaseHasRecords() {
    List<RawMaterial> result = adapter.listAllRawMaterials();

    assertFalse(result.isEmpty());
    assertTrue(result.size() >= 19);
    assertTrue(result.stream().anyMatch(r -> "MAD-001".equals(r.getCode())));
  }

  @Test
  @Transactional
  void save_shouldFail_whenCodeAlreadyExists() {
    RawMaterial duplicate = RawMaterialFixture.createRawMaterial(null);
    duplicate.setCode("MAD-001");
    duplicate.setName("Duplicate Code");
    duplicate.setStockQuantity(new BigDecimal("10.00"));

    assertThrows(PersistenceException.class, () -> adapter.save(duplicate));
  }

  @Test
  @Transactional
  void save_shouldFail_whenCodeIsNull() {
    RawMaterial invalid = createNewRawMaterial("Null Code", "10.00");
    invalid.setCode(null);

    assertThrows(RuntimeException.class, () -> adapter.save(invalid));
  }

  @Test
  @Transactional
  void save_shouldFail_whenNameIsNull() {
    RawMaterial invalid = createNewRawMaterial("Will Be Null", "10.00");
    invalid.setName(null);

    assertThrows(RuntimeException.class, () -> adapter.save(invalid));
  }

  @Test
  @Transactional
  void save_shouldFail_whenStockQuantityIsNull() {
    RawMaterial invalid = createNewRawMaterial("Null Stock", "10.00");
    invalid.setStockQuantity(null);

    assertThrows(RuntimeException.class, () -> adapter.save(invalid));
  }

  private static String uniqueCode() {
    return "IT-RM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }

  private static RawMaterial createNewRawMaterial(String name, String stockQuantity) {
    RawMaterial rawMaterial = RawMaterialFixture.createRawMaterial(null);
    rawMaterial.setCode(uniqueCode());
    rawMaterial.setName(name);
    rawMaterial.setStockQuantity(new BigDecimal(stockQuantity));
    return rawMaterial;
  }
}
