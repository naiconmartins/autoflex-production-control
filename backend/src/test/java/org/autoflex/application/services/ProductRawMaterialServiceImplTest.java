package org.autoflex.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.autoflex.application.commands.ProductRawMaterialCommand;
import org.autoflex.application.gateways.ProductRawMaterialRepository;
import org.autoflex.application.gateways.ProductRepository;
import org.autoflex.application.gateways.RawMaterialRepository;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.domain.Product;
import org.autoflex.domain.ProductRawMaterial;
import org.autoflex.domain.RawMaterial;
import org.autoflex.fixtures.ProductRawMaterialFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductRawMaterialServiceImplTest {

  @Mock ProductRawMaterialRepository repository;
  @Mock ProductRepository productRepository;
  @Mock RawMaterialRepository rawMaterialRepository;

  @InjectMocks ProductRawMaterialServiceImpl service;

  private Long productId;
  private Long rawMaterialId;
  private Product product;
  private RawMaterial rawMaterial;
  private ProductRawMaterialCommand cmd;

  @BeforeEach
  void setUp() {
    productId = 1L;
    rawMaterialId = 2L;
    product = ProductRawMaterialFixture.createProduct(productId);
    rawMaterial = ProductRawMaterialFixture.createRawMaterial(rawMaterialId);
    cmd = ProductRawMaterialFixture.createCommand(rawMaterialId, new BigDecimal("10.00"));
  }

  @Test
  void add_shouldCreateAssociation_whenValidRequest() {
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(rawMaterialRepository.findById(rawMaterialId)).thenReturn(Optional.of(rawMaterial));
    when(repository.findByProductAndRawMaterial(productId, rawMaterialId))
        .thenReturn(Optional.empty());
    when(repository.save(any(ProductRawMaterial.class))).thenAnswer(i -> i.getArgument(0));

    ProductRawMaterial result = service.add(productId, cmd);

    assertNotNull(result);
    assertEquals(productId, result.getProduct().getId());
    assertEquals(rawMaterialId, result.getRawMaterial().getId());
    assertEquals(0, cmd.requiredQuantity().compareTo(result.getRequiredQuantity()));
    verify(repository).save(any(ProductRawMaterial.class));
  }

  @Test
  void add_shouldThrowResourceNotFoundException_whenProductNotExists() {
    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> service.add(productId, cmd));

    assertEquals("Product with id 1 not found", ex.getMessage());
    verify(rawMaterialRepository, never()).findById(any());
    verify(repository, never()).findByProductAndRawMaterial(any(), any());
    verify(repository, never()).save(any());
  }

  @Test
  void add_shouldThrowResourceNotFoundException_whenRawMaterialNotExists() {
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(rawMaterialRepository.findById(rawMaterialId)).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> service.add(productId, cmd));

    assertEquals("Raw material with id 2 not found", ex.getMessage());
    verify(repository, never()).findByProductAndRawMaterial(any(), any());
    verify(repository, never()).save(any());
  }

  @Test
  void add_shouldThrowConflictException_whenAssociationAlreadyExists() {
    ProductRawMaterial existing =
        ProductRawMaterialFixture.createLink(10L, productId, rawMaterialId, new BigDecimal("5.00"));

    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(rawMaterialRepository.findById(rawMaterialId)).thenReturn(Optional.of(rawMaterial));
    when(repository.findByProductAndRawMaterial(productId, rawMaterialId))
        .thenReturn(Optional.of(existing));

    ConflictException ex = assertThrows(ConflictException.class, () -> service.add(productId, cmd));

    assertEquals("Raw material already linked to product", ex.getMessage());
    verify(repository, never()).save(any());
  }

  @Test
  void listByProduct_shouldReturnAssociations_whenProductExists() {
    List<ProductRawMaterial> expected =
        List.of(
            ProductRawMaterialFixture.createLink(
                1L, productId, rawMaterialId, new BigDecimal("10.00")));

    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(repository.listByProduct(productId)).thenReturn(expected);

    List<ProductRawMaterial> result = service.listByProduct(productId);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(1L, result.getFirst().getId());
    assertEquals(productId, result.getFirst().getProduct().getId());
  }

  @Test
  void listByProduct_shouldReturnEmptyList_whenProductHasNoAssociations() {
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(repository.listByProduct(productId)).thenReturn(List.of());

    List<ProductRawMaterial> result = service.listByProduct(productId);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void listByProduct_shouldThrowResourceNotFoundException_whenProductNotExists() {
    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> service.listByProduct(productId));

    assertEquals("Product with id 1 not found", ex.getMessage());
    verify(repository, never()).listByProduct(any());
  }

  @Test
  void updateRequiredQuantity_shouldUpdateAssociation_whenValidRequest() {
    ProductRawMaterial existing =
        ProductRawMaterialFixture.createLink(1L, productId, rawMaterialId, new BigDecimal("10.00"));
    ProductRawMaterialCommand updateCmd =
        ProductRawMaterialFixture.createCommand(rawMaterialId, new BigDecimal("15.00"));

    when(repository.findByProductAndRawMaterial(productId, rawMaterialId))
        .thenReturn(Optional.of(existing));
    when(repository.save(any(ProductRawMaterial.class))).thenAnswer(i -> i.getArgument(0));

    ProductRawMaterial result = service.updateRequiredQuantity(productId, rawMaterialId, updateCmd);

    assertNotNull(result);
    assertEquals(0, new BigDecimal("15.00").compareTo(result.getRequiredQuantity()));
    verify(repository).save(existing);
  }

  @Test
  void updateRequiredQuantity_shouldThrowResourceNotFoundException_whenAssociationNotExists() {
    when(repository.findByProductAndRawMaterial(productId, rawMaterialId))
        .thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(
            ResourceNotFoundException.class,
            () -> service.updateRequiredQuantity(productId, rawMaterialId, cmd));

    assertEquals("Association not found between Product 1 and Raw Material 2", ex.getMessage());
    verify(repository, never()).save(any());
  }

  @Test
  void remove_shouldDeleteAssociation_whenValidRequest() {
    ProductRawMaterial existing =
        ProductRawMaterialFixture.createLink(1L, productId, rawMaterialId, new BigDecimal("10.00"));

    when(repository.findByProductAndRawMaterial(productId, rawMaterialId))
        .thenReturn(Optional.of(existing));

    service.remove(productId, rawMaterialId);

    verify(repository).delete(1L);
  }

  @Test
  void remove_shouldThrowResourceNotFoundException_whenAssociationNotExists() {
    when(repository.findByProductAndRawMaterial(productId, rawMaterialId))
        .thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(
            ResourceNotFoundException.class, () -> service.remove(productId, rawMaterialId));

    assertEquals(
        "Association not found between Product ID 1 and Raw Material ID 2", ex.getMessage());
    verify(repository, never()).delete(any());
  }
}
