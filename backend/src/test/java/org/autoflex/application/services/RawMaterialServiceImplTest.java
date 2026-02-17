package org.autoflex.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.autoflex.application.commands.RawMaterialCommand;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.application.dto.SearchQuery;
import org.autoflex.application.gateways.RawMaterialRepository;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.DatabaseException;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.domain.RawMaterial;
import org.autoflex.fixtures.RawMaterialFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RawMaterialServiceImplTest {

  @Mock RawMaterialRepository rawMaterialRepository;

  @InjectMocks RawMaterialServiceImpl rawMaterialService;

  private RawMaterialCommand cmd;
  private Long existingId;

  @BeforeEach
  void setUp() {
    cmd = RawMaterialFixture.createValidRawMaterialCommand();
    existingId = 1L;
  }

  @Test
  void insert_shouldCreateRawMaterial_whenValidRequest() {
    when(rawMaterialRepository.findByCode(cmd.code())).thenReturn(Optional.empty());
    when(rawMaterialRepository.save(any(RawMaterial.class))).thenAnswer(i -> i.getArgument(0));

    RawMaterial result = rawMaterialService.insert(cmd);

    assertNotNull(result);
    assertEquals(cmd.code(), result.getCode());
    assertEquals(cmd.name(), result.getName());
    assertEquals(0, result.getStockQuantity().compareTo(cmd.stockQuantity()));
    verify(rawMaterialRepository).save(any(RawMaterial.class));
  }

  @Test
  void insert_shouldThrowConflictException_whenCodeAlreadyExists() {
    RawMaterial existing = RawMaterialFixture.createRawMaterial(2L);
    when(rawMaterialRepository.findByCode(cmd.code())).thenReturn(Optional.of(existing));

    ConflictException ex = assertThrows(ConflictException.class, () -> rawMaterialService.insert(cmd));

    assertEquals("Raw material code already exists", ex.getMessage());
    verify(rawMaterialRepository, never()).save(any(RawMaterial.class));
  }

  @Test
  void update_shouldUpdateRawMaterial_whenValidRequest() {
    RawMaterial existing = RawMaterialFixture.createRawMaterial(existingId);

    when(rawMaterialRepository.findById(existingId)).thenReturn(Optional.of(existing));
    when(rawMaterialRepository.findByCode(cmd.code())).thenReturn(Optional.empty());
    when(rawMaterialRepository.save(any(RawMaterial.class))).thenAnswer(i -> i.getArgument(0));

    RawMaterial result = rawMaterialService.update(existingId, cmd);

    assertNotNull(result);
    assertEquals(cmd.code(), result.getCode());
    assertEquals(cmd.name(), result.getName());
    assertEquals(0, result.getStockQuantity().compareTo(cmd.stockQuantity()));
    verify(rawMaterialRepository).save(any(RawMaterial.class));
  }

  @Test
  void update_shouldThrowResourceNotFoundException_whenIdNotExists() {
    when(rawMaterialRepository.findById(existingId)).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> rawMaterialService.update(existingId, cmd));

    assertEquals("Raw material with id 1 not found", ex.getMessage());
    verify(rawMaterialRepository, never()).findByCode(cmd.code());
    verify(rawMaterialRepository, never()).save(any(RawMaterial.class));
  }

  @Test
  void update_shouldThrowConflictException_whenCodeAlreadyExistsForAnotherRawMaterial() {
    RawMaterial existing = RawMaterialFixture.createRawMaterial(existingId);
    RawMaterial otherWithSameCode = RawMaterialFixture.createRawMaterial(2L);

    when(rawMaterialRepository.findById(existingId)).thenReturn(Optional.of(existing));
    when(rawMaterialRepository.findByCode(cmd.code())).thenReturn(Optional.of(otherWithSameCode));

    ConflictException ex =
        assertThrows(ConflictException.class, () -> rawMaterialService.update(existingId, cmd));

    assertEquals("Raw material code already exists", ex.getMessage());
    verify(rawMaterialRepository, never()).save(any(RawMaterial.class));
  }

  @Test
  void update_shouldUpdateRawMaterial_whenCodeBelongsToSameRawMaterial() {
    RawMaterial existing = RawMaterialFixture.createRawMaterial(existingId);
    RawMaterial sameByCode = RawMaterialFixture.createRawMaterial(existingId);

    when(rawMaterialRepository.findById(existingId)).thenReturn(Optional.of(existing));
    when(rawMaterialRepository.findByCode(cmd.code())).thenReturn(Optional.of(sameByCode));
    when(rawMaterialRepository.save(any(RawMaterial.class))).thenAnswer(i -> i.getArgument(0));

    RawMaterial result = rawMaterialService.update(existingId, cmd);

    assertNotNull(result);
    assertEquals(cmd.code(), result.getCode());
    verify(rawMaterialRepository).save(any(RawMaterial.class));
  }

  @Test
  void delete_shouldDeleteRawMaterial_whenValidRequest() {
    RawMaterial existing = RawMaterialFixture.createRawMaterial(existingId);
    when(rawMaterialRepository.findById(existingId)).thenReturn(Optional.of(existing));

    rawMaterialService.delete(existingId);

    verify(rawMaterialRepository).findById(existingId);
    verify(rawMaterialRepository).delete(existingId);
  }

  @Test
  void delete_shouldThrowResourceNotFoundException_whenIdNotExists() {
    when(rawMaterialRepository.findById(existingId)).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> rawMaterialService.delete(existingId));

    assertEquals("Raw material with id 1 not found", ex.getMessage());
    verify(rawMaterialRepository, never()).delete(existingId);
  }

  @Test
  void delete_shouldThrowDatabaseException_whenDeleteFails() {
    RawMaterial existing = RawMaterialFixture.createRawMaterial(existingId);
    when(rawMaterialRepository.findById(existingId)).thenReturn(Optional.of(existing));
    doThrow(new RuntimeException("fk violation")).when(rawMaterialRepository).delete(existingId);

    DatabaseException ex =
        assertThrows(DatabaseException.class, () -> rawMaterialService.delete(existingId));

    assertEquals("Cannot delete raw material because it is referenced by other records", ex.getMessage());
  }

  @Test
  void findAll_shouldReturnRawMaterials_whenValidRequest() {
    SearchQuery query = new SearchQuery(0, 10, "name", "asc");
    PagedModel<RawMaterial> expected =
        new PagedModel<>(List.of(RawMaterialFixture.createRawMaterial(1L)), 1L, 1);

    when(rawMaterialRepository.findAll(query)).thenReturn(expected);

    PagedModel<RawMaterial> result = rawMaterialService.findAll(query);

    assertNotNull(result);
    assertEquals(1L, result.totalElements());
    assertEquals(1, result.totalPages());
    assertEquals(1, result.items().size());
  }

  @Test
  void findAll_shouldReturnEmptyPage_whenNoRawMaterials() {
    SearchQuery query = new SearchQuery(0, 10, "name", "asc");
    PagedModel<RawMaterial> expected = new PagedModel<>(List.of(), 0L, 0);

    when(rawMaterialRepository.findAll(query)).thenReturn(expected);

    PagedModel<RawMaterial> result = rawMaterialService.findAll(query);

    assertNotNull(result);
    assertEquals(0L, result.totalElements());
    assertEquals(0, result.totalPages());
    assertTrue(result.items().isEmpty());
  }

  @Test
  void findById_shouldReturnRawMaterial_whenIdExists() {
    RawMaterial existing = RawMaterialFixture.createRawMaterial(existingId);
    when(rawMaterialRepository.findById(existingId)).thenReturn(Optional.of(existing));

    RawMaterial result = rawMaterialService.findById(existingId);

    assertNotNull(result);
    assertEquals(existingId, result.getId());
  }

  @Test
  void findById_shouldThrowResourceNotFoundException_whenIdNotExists() {
    when(rawMaterialRepository.findById(existingId)).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> rawMaterialService.findById(existingId));

    assertEquals("Raw material with id 1 not found", ex.getMessage());
  }

  @Test
  void findByName_shouldReturnRawMaterials_whenValidRequest() {
    SearchQuery query = new SearchQuery(0, 10, "name", "asc");
    PagedModel<RawMaterial> expected =
        new PagedModel<>(List.of(RawMaterialFixture.createRawMaterial(1L)), 1L, 1);

    when(rawMaterialRepository.findByName("MDF", query)).thenReturn(expected);

    PagedModel<RawMaterial> result = rawMaterialService.findByName("MDF", query);

    assertNotNull(result);
    assertEquals(1L, result.totalElements());
    assertEquals(1, result.items().size());
  }

  @Test
  void findByName_shouldReturnEmptyPage_whenNoMatch() {
    SearchQuery query = new SearchQuery(0, 10, "name", "asc");
    PagedModel<RawMaterial> expected = new PagedModel<>(List.of(), 0L, 0);

    when(rawMaterialRepository.findByName("NOT-FOUND", query)).thenReturn(expected);

    PagedModel<RawMaterial> result = rawMaterialService.findByName("NOT-FOUND", query);

    assertNotNull(result);
    assertEquals(0L, result.totalElements());
    assertEquals(0, result.totalPages());
    assertTrue(result.items().isEmpty());
  }
}
