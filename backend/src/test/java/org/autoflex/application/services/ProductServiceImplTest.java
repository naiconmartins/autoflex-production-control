package org.autoflex.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.autoflex.application.commands.ProductCommand;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.application.dto.SearchQuery;
import org.autoflex.application.gateways.ProductRepository;
import org.autoflex.application.gateways.RawMaterialRepository;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.DatabaseException;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.domain.Product;
import org.autoflex.domain.ProductRawMaterial;
import org.autoflex.domain.RawMaterial;
import org.autoflex.fixtures.ProductFixture;
import org.autoflex.fixtures.RawMaterialFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

  @Mock ProductRepository productRepository;
  @Mock RawMaterialRepository rawMaterialRepository;

  @InjectMocks ProductServiceImpl productService;

  private ProductCommand cmd;
  private RawMaterial rm;
  private Long existingId;

  @BeforeEach
  void setUp() {
    cmd = ProductFixture.createValidProductCommand();
    existingId = 1L;
    rm = RawMaterialFixture.createRawMaterial(1L);
  }

  @Test
  void insert_shouldCreateProduct_whenValidRequest() {
    when(productRepository.findByCode(cmd.code())).thenReturn(Optional.empty());
    when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(rm));
    when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

    Product result = productService.insert(cmd);

    assertNotNull(result);
    assertEquals(cmd.code(), result.getCode());
    assertNotNull(result.getRawMaterials());
    assertEquals(1, result.getRawMaterials().size());

    ProductRawMaterial item = result.getRawMaterials().getFirst();
    assertEquals(1L, item.getRawMaterial().getId());
    assertEquals(0, item.getRequiredQuantity().compareTo(new BigDecimal("150.00")));

    verify(productRepository).save(any(Product.class));
  }

  @Test
  void insert_shouldCreateProduct_whenRawMaterialsIsNull() {
    ProductCommand cmdWithoutRawMaterials =
        new ProductCommand(cmd.code(), cmd.name(), cmd.price(), null);
    when(productRepository.findByCode(cmdWithoutRawMaterials.code())).thenReturn(Optional.empty());
    when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

    Product result = productService.insert(cmdWithoutRawMaterials);

    assertNotNull(result);
    assertTrue(result.getRawMaterials().isEmpty());
    verify(productRepository).save(any(Product.class));
    verifyNoInteractions(rawMaterialRepository);
  }

  @Test
  void insert_shouldCreateProduct_whenRawMaterialsIsEmpty() {
    ProductCommand cmdWithoutRawMaterials =
        new ProductCommand(cmd.code(), cmd.name(), cmd.price(), List.of());
    when(productRepository.findByCode(cmdWithoutRawMaterials.code())).thenReturn(Optional.empty());
    when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

    Product result = productService.insert(cmdWithoutRawMaterials);

    assertNotNull(result);
    assertTrue(result.getRawMaterials().isEmpty());
    verify(productRepository).save(any(Product.class));
    verifyNoInteractions(rawMaterialRepository);
  }

  @Test
  void insert_shouldCreateProduct_whenCodeAlreadyExist() {
    Product existing = ProductFixture.createProduct();
    when(productRepository.findByCode(cmd.code())).thenReturn(Optional.of(existing));

    ConflictException ex = assertThrows(ConflictException.class, () -> productService.insert(cmd));

    assertEquals("Product code already exists", ex.getMessage());
    verify(productRepository).findByCode(cmd.code());
    verifyNoInteractions(rawMaterialRepository);
    verify(productRepository, never()).save(any(Product.class));
  }

  @Test
  void insert_shouldCreateProduct_whenRawMaterialNotExist() {
    when(productRepository.findByCode(cmd.code())).thenReturn(Optional.empty());
    when(rawMaterialRepository.findById(1L)).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> productService.insert(cmd));

    assertEquals("Raw Material not found: 1", ex.getMessage());
    verify(productRepository).findByCode(cmd.code());
    verify(productRepository, never()).save(any(Product.class));
  }

  @Test
  void update_shouldUpdateProduct_whenValidRequest() {
    Product existing = ProductFixture.createProduct();
    existing.setId(existingId);

    when(productRepository.findById(existingId)).thenReturn(Optional.of(existing));
    when(productRepository.findByCode(cmd.code())).thenReturn(Optional.empty());
    when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(rm));
    when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

    Product result = productService.update(existingId, cmd);

    assertNotNull(result);
    assertEquals(cmd.code(), result.getCode());
    assertNotNull(result.getRawMaterials());
    assertEquals(1, result.getRawMaterials().size());

    ProductRawMaterial item = result.getRawMaterials().getFirst();
    assertEquals(1L, item.getRawMaterial().getId());
    assertEquals(0, item.getRequiredQuantity().compareTo(new BigDecimal("150.00")));

    verify(productRepository).save(existing);
  }

  @Test
  void update_shouldUpdateProduct_whenIdNotExist() {
    when(productRepository.findById(2L)).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> productService.update(2L, cmd));

    assertEquals("Product with id 2 not found", ex.getMessage());
    verify(productRepository, never()).findByCode(cmd.code());
    verifyNoInteractions(rawMaterialRepository);
    verify(productRepository, never()).save(any(Product.class));
  }

  @Test
  void update_shouldUpdateProduct_whenCodeAlreadyExist() {
    Product current = ProductFixture.createProduct();
    current.setId(existingId);

    Product otherWithSameCode = ProductFixture.createProduct();
    otherWithSameCode.setId(2L);

    when(productRepository.findById(existingId)).thenReturn(Optional.of(current));
    when(productRepository.findByCode(cmd.code())).thenReturn(Optional.of(otherWithSameCode));

    ConflictException ex =
        assertThrows(ConflictException.class, () -> productService.update(existingId, cmd));

    assertEquals("Product code already exists", ex.getMessage());
    verify(productRepository).findById(existingId);
    verify(productRepository).findByCode(cmd.code());
    verifyNoInteractions(rawMaterialRepository);
    verify(productRepository, never()).save(any(Product.class));
  }

  @Test
  void update_shouldUpdateProduct_whenCodeBelongsToSameProduct() {
    Product current = ProductFixture.createProduct();
    current.setId(existingId);

    Product sameProductByCode = ProductFixture.createProduct();
    sameProductByCode.setId(existingId);

    when(productRepository.findById(existingId)).thenReturn(Optional.of(current));
    when(productRepository.findByCode(cmd.code())).thenReturn(Optional.of(sameProductByCode));
    when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(rm));
    when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

    Product result = productService.update(existingId, cmd);

    assertNotNull(result);
    assertEquals(cmd.code(), result.getCode());
    verify(productRepository).save(current);
  }

  @Test
  void update_shouldUpdateProduct_whenRawMaterialsIsNull() {
    Product existing = ProductFixture.createProduct();
    existing.setId(existingId);
    existing.addRawMaterial(rm, new BigDecimal("10.00"));
    ProductCommand cmdWithoutRawMaterials =
        new ProductCommand(cmd.code(), cmd.name(), cmd.price(), null);

    when(productRepository.findById(existingId)).thenReturn(Optional.of(existing));
    when(productRepository.findByCode(cmdWithoutRawMaterials.code())).thenReturn(Optional.empty());
    when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

    Product result = productService.update(existingId, cmdWithoutRawMaterials);

    assertNotNull(result);
    assertTrue(result.getRawMaterials().isEmpty());
    verify(productRepository).save(existing);
    verifyNoInteractions(rawMaterialRepository);
  }

  @Test
  void update_shouldUpdateProduct_whenRawMaterialNotExist() {
    Product existing = ProductFixture.createProduct();
    existing.setId(existingId);

    when(productRepository.findById(existingId)).thenReturn(Optional.of(existing));
    when(productRepository.findByCode(cmd.code())).thenReturn(Optional.empty());
    when(rawMaterialRepository.findById(1L)).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> productService.update(existingId, cmd));

    assertEquals("Raw Material not found: 1", ex.getMessage());
    verify(productRepository).findByCode(cmd.code());
    verify(productRepository, never()).save(any(Product.class));
  }

  @Test
  void delete_shouldDeleteProduct_whenValidRequest() {
    Product existing = ProductFixture.createProduct();
    existing.setId(existingId);

    when(productRepository.findById(existingId)).thenReturn(Optional.of(existing));

    assertDoesNotThrow(() -> productService.delete(existingId));

    verify(productRepository).findById(existingId);
    verify(productRepository).delete(existingId);
  }

  @Test
  void delete_shouldThrowResourceNotFoundException_whenIdNotExist() {
    when(productRepository.findById(existingId)).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> productService.delete(existingId));

    assertEquals("Product with id 1 not found", ex.getMessage());
    verify(productRepository).findById(existingId);
    verify(productRepository, never()).delete(existingId);
  }

  @Test
  void delete_shouldThrowDatabaseException_whenDeleteFails() {
    Product existing = ProductFixture.createProduct();
    existing.setId(existingId);

    when(productRepository.findById(existingId)).thenReturn(Optional.of(existing));
    doThrow(new RuntimeException("fk violation")).when(productRepository).delete(existingId);

    DatabaseException ex =
        assertThrows(DatabaseException.class, () -> productService.delete(existingId));

    assertEquals("Cannot delete product because it is referenced by other records", ex.getMessage());
    verify(productRepository).delete(existingId);
  }

  @Test
  void findAll_shouldReturnAllProducts_whenValidRequest() {
    SearchQuery query = new SearchQuery(0, 10, "name", "asc");
    PagedModel<Product> expected = new PagedModel<>(List.of(ProductFixture.createProduct()), 1L, 1);

    when(productRepository.findAll(query)).thenReturn(expected);

    PagedModel<Product> result = productService.findAll(query);

    assertNotNull(result);
    assertEquals(1L, result.totalElements());
    assertEquals(1, result.totalPages());
    assertEquals(1, result.items().size());
    verify(productRepository).findAll(query);
  }

  @Test
  void findAll_shouldReturnEmptyPage_whenNoProducts() {
    SearchQuery query = new SearchQuery(0, 10, "name", "asc");
    PagedModel<Product> expected = new PagedModel<>(List.of(), 0L, 0);

    when(productRepository.findAll(query)).thenReturn(expected);

    PagedModel<Product> result = productService.findAll(query);

    assertNotNull(result);
    assertEquals(0L, result.totalElements());
    assertEquals(0, result.totalPages());
    assertTrue(result.items().isEmpty());
    verify(productRepository).findAll(query);
  }

  @Test
  void findById_shouldReturnProduct_whenIdExists() {
    Product existing = ProductFixture.createProduct();
    existing.setId(existingId);
    when(productRepository.findById(existingId)).thenReturn(Optional.of(existing));

    Product result = productService.findById(existingId);

    assertNotNull(result);
    assertEquals(existingId, result.getId());
    verify(productRepository).findById(existingId);
  }

  @Test
  void findById_shouldThrowResourceNotFoundException_whenIdNotExist() {
    when(productRepository.findById(existingId)).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> productService.findById(existingId));

    assertEquals("Product not found with id: 1", ex.getMessage());
    verify(productRepository).findById(existingId);
  }

  @Test
  void findByName_shouldReturnProducts_whenValidRequest() {
    SearchQuery query = new SearchQuery(0, 10, "name", "asc");
    String name = "Dining";
    PagedModel<Product> expected = new PagedModel<>(List.of(ProductFixture.createProduct()), 1L, 1);

    when(productRepository.findByName(name, query)).thenReturn(expected);

    PagedModel<Product> result = productService.findByName(name, query);

    assertNotNull(result);
    assertEquals(1L, result.totalElements());
    assertEquals(1, result.items().size());
    verify(productRepository).findByName(name, query);
  }

  @Test
  void findByName_shouldReturnEmptyPage_whenNoMatch() {
    SearchQuery query = new SearchQuery(0, 10, "name", "asc");
    String name = "NotFound";
    PagedModel<Product> expected = new PagedModel<>(List.of(), 0L, 0);

    when(productRepository.findByName(name, query)).thenReturn(expected);

    PagedModel<Product> result = productService.findByName(name, query);

    assertNotNull(result);
    assertEquals(0L, result.totalElements());
    assertEquals(0, result.totalPages());
    assertTrue(result.items().isEmpty());
    verify(productRepository).findByName(name, query);
  }
}
