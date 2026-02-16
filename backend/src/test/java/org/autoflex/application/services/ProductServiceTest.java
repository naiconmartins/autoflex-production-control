package org.autoflex.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;
import org.autoflex.adapters.inbound.dto.request.PageRequestDTO;
import org.autoflex.adapters.inbound.dto.request.ProductRequestDTO;
import org.autoflex.adapters.inbound.dto.response.PageResponseDTO;
import org.autoflex.adapters.inbound.dto.response.ProductResponseDTO;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.DatabaseException;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.domain.Product;
import org.autoflex.domain.ProductRawMaterial;
import org.autoflex.domain.RawMaterial;
import org.autoflex.factory.ProductFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductServiceTest {

  @Inject ProductService productService;

  @InjectMock EntityManager entityManager;

  private Long id;
  private ProductRequestDTO dto;
  private Product existingProduct;

  @BeforeEach
  void setup() {
    id = 1L;
    dto = ProductFactory.createProductRequestDTO();
    existingProduct = ProductFactory.createProductWithCode(dto.code);
    existingProduct.setId(id);

    PanacheMock.mock(Product.class);
    PanacheMock.mock(RawMaterial.class);
    when(Product.getEntityManager()).thenReturn(entityManager);
    when(RawMaterial.getEntityManager()).thenReturn(entityManager);
  }

  private void mockFindByCode(Product result) {
    PanacheQuery<Product> mockQuery = mock(PanacheQuery.class);
    doReturn(result).when(mockQuery).firstResult();
    doReturn(Optional.ofNullable(result)).when(mockQuery).firstResultOptional();

    PanacheMock.doReturn(mockQuery).when(Product.class).find(eq("code"), any(Object[].class));
  }

  private void mockFindById(Long id, Product result) {
    when(Product.findById(id)).thenReturn(result);
  }

  private void mockRawMaterialFindById(Long id, RawMaterial result) {
    when(RawMaterial.findById(id)).thenReturn(result);
  }

  private PanacheQuery<Product> mockPanacheQuery(List<Product> items, long count, int pageCount) {
    PanacheQuery<Product> mockQuery = mock(PanacheQuery.class);
    doReturn(mockQuery).when(mockQuery).page(any(Page.class));
    when(mockQuery.list()).thenReturn(items);
    when(mockQuery.count()).thenReturn(count);
    when(mockQuery.pageCount()).thenReturn(pageCount);
    return mockQuery;
  }

  private void mockFindAll(List<Product> products, long count, int pageCount) {
    PanacheQuery<Product> mockQuery = mockPanacheQuery(products, count, pageCount);
    PanacheMock.doReturn(mockQuery).when(Product.class).findAll(any(Sort.class));
  }

  private void mockFindByName(List<Product> products, long count, int pageCount) {
    PanacheQuery<Product> mockQuery = mockPanacheQuery(products, count, pageCount);
    PanacheMock.doReturn(mockQuery)
        .when(Product.class)
        .find(
            eq("lower(name) like concat('%', lower(?1), '%')"),
            any(Sort.class),
            any(Object[].class));
  }

  private void assertProductEquals(ProductResponseDTO result, ProductRequestDTO expected) {
    assertNotNull(result);
    assertEquals(expected.code, result.code);
    assertEquals(expected.name, result.name);
    assertEquals(expected.price, result.price);
  }

  @Test
  void insert_shouldCreateProduct_whenValidRequest() {
    mockFindByCode(null);
    doNothing().when(entityManager).persist(any(Product.class));

    ProductResponseDTO result = productService.insert(dto);

    assertProductEquals(result, dto);
    verify(entityManager, times(1)).persist(any(Product.class));
  }

  @Test
  void insert_shouldCreateProductWithRawMaterials_whenValidRequestHasRawMaterials() {
    ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();
    mockFindByCode(null);

    RawMaterial rm1 = new RawMaterial("RM001", "Raw Material 1", dto.price);
    rm1.setId(1L);
    RawMaterial rm2 = new RawMaterial("RM002", "Raw Material 2", dto.price);
    rm2.setId(2L);

    mockRawMaterialFindById(1L, rm1);
    mockRawMaterialFindById(2L, rm2);
    doNothing().when(entityManager).persist(any(Product.class));

    ProductResponseDTO result = productService.insert(request);

    assertNotNull(result);
    assertEquals(2, result.rawMaterials.size());
    assertEquals(1L, result.rawMaterials.get(0).id);
    assertEquals(
        request.rawMaterials.get(0).requiredQuantity, result.rawMaterials.get(0).requiredQuantity);
    assertEquals(2L, result.rawMaterials.get(1).id);
    assertEquals(
        request.rawMaterials.get(1).requiredQuantity, result.rawMaterials.get(1).requiredQuantity);
    verify(entityManager, times(1)).persist(any(Product.class));
  }

  @Test
  void insert_shouldThrowResourceNotFoundException_whenRawMaterialDoesNotExist() {
    ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();
    mockFindByCode(null);
    mockRawMaterialFindById(1L, null);

    assertThrows(ResourceNotFoundException.class, () -> productService.insert(request));
    verify(entityManager, never()).persist(any());
  }

  @Test
  void insert_shouldThrowConflictException_whenCodeAlreadyExists() {
    PanacheQuery<Product> mockQuery = mock(PanacheQuery.class);
    when(mockQuery.firstResultOptional()).thenReturn(Optional.of(existingProduct));

    PanacheMock.doReturn(mockQuery).when(Product.class).find(eq("code"), any(Object[].class));

    assertThrows(ConflictException.class, () -> productService.insert(dto));
    verify(entityManager, never()).persist(any());
  }

  @Test
  void update_shouldUpdateProduct_whenValidRequest() {
    mockFindById(id, existingProduct);
    mockFindByCode(null);

    ProductResponseDTO result = productService.update(id, dto);

    assertProductEquals(result, dto);
    assertEquals(dto.code, existingProduct.getCode());
    assertEquals(dto.name, existingProduct.getName());
    assertEquals(dto.price, existingProduct.getPrice());
  }

  @Test
  void update_shouldReplaceRawMaterials_whenRawMaterialsProvided() {
    ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

    RawMaterial oldRm = new RawMaterial("RM-OLD", "Old RM", dto.price);
    oldRm.setId(99L);
    existingProduct.addRawMaterial(new ProductRawMaterial(existingProduct, oldRm, dto.price));

    RawMaterial rm1 = new RawMaterial("RM001", "Raw Material 1", dto.price);
    rm1.setId(1L);
    RawMaterial rm2 = new RawMaterial("RM002", "Raw Material 2", dto.price);
    rm2.setId(2L);

    mockFindById(id, existingProduct);
    mockFindByCode(null);
    mockRawMaterialFindById(1L, rm1);
    mockRawMaterialFindById(2L, rm2);

    ProductResponseDTO result = productService.update(id, request);

    assertNotNull(result);
    assertEquals(2, result.rawMaterials.size());
    assertEquals(2, existingProduct.getRawMaterials().size());
    assertEquals(1L, result.rawMaterials.get(0).id);
    assertEquals(
        request.rawMaterials.get(0).requiredQuantity, result.rawMaterials.get(0).requiredQuantity);
    assertEquals(2L, result.rawMaterials.get(1).id);
    assertEquals(
        request.rawMaterials.get(1).requiredQuantity, result.rawMaterials.get(1).requiredQuantity);
    verify(entityManager, times(1)).flush();
  }

  @Test
  void update_shouldThrowResourceNotFoundException_whenRawMaterialDoesNotExist() {
    ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

    mockFindById(id, existingProduct);
    mockFindByCode(null);
    mockRawMaterialFindById(1L, null);

    assertThrows(ResourceNotFoundException.class, () -> productService.update(id, request));
    verify(entityManager, times(1)).flush();
  }

  @Test
  void update_shouldClearRawMaterials_whenRawMaterialsIsNull() {
    existingProduct.addRawMaterial(
        new ProductRawMaterial(existingProduct, new RawMaterial(), dto.price));

    mockFindById(id, existingProduct);
    mockFindByCode(null);
    dto.rawMaterials = null;

    ProductResponseDTO result = productService.update(id, dto);

    assertNotNull(result);
    assertTrue(existingProduct.getRawMaterials().isEmpty());
    verify(entityManager, times(1)).flush();
  }

  @Test
  void update_shouldClearRawMaterials_whenRawMaterialsIsEmpty() {
    existingProduct.addRawMaterial(
        new ProductRawMaterial(existingProduct, new RawMaterial(), dto.price));
    ProductRequestDTO request = ProductFactory.createProductRequestDTO();

    mockFindById(id, existingProduct);
    mockFindByCode(null);

    ProductResponseDTO result = productService.update(id, request);

    assertNotNull(result);
    assertTrue(existingProduct.getRawMaterials().isEmpty());
    verify(entityManager, times(1)).flush();
  }

  @Test
  void update_shouldThrowResourceNotFoundException_whenIdNotFound() {
    mockFindById(id, null);

    assertThrows(ResourceNotFoundException.class, () -> productService.update(id, dto));
  }

  @Test
  void update_shouldThrowConflictException_whenCodeExistsInOtherEntity() {
    Product otherProduct = ProductFactory.createProductWithCode(dto.code);
    otherProduct.setId(2L);

    mockFindById(id, existingProduct);
    mockFindByCode(otherProduct);

    assertThrows(ConflictException.class, () -> productService.update(id, dto));
  }

  @Test
  void update_shouldAllowSameCode_whenUpdatingSameEntity() {
    mockFindById(id, existingProduct);
    mockFindByCode(existingProduct);

    ProductResponseDTO result = productService.update(id, dto);

    assertNotNull(result);
    assertEquals(dto.code, result.code);
  }

  @Test
  void delete_shouldDeleteProduct_whenValidId() {
    mockFindById(id, existingProduct);
    when(PanacheMock.getMock(Product.class).deleteById(id)).thenReturn(true);

    assertDoesNotThrow(() -> productService.delete(id));
  }

  @Test
  void delete_shouldThrowResourceNotFoundException_whenIdNotFound() {
    mockFindById(id, null);

    assertThrows(ResourceNotFoundException.class, () -> productService.delete(id));
  }

  @Test
  void delete_shouldThrowDatabaseException_whenEntityIsReferenced() {
    mockFindById(id, existingProduct);

    doThrow(new PersistenceException("Constraint violation"))
        .when(PanacheMock.getMock(Product.class))
        .deleteById(id);

    DatabaseException exception =
        assertThrows(DatabaseException.class, () -> productService.delete(id));
    assertEquals(
        "Cannot delete product because it is referenced by other records", exception.getMessage());
  }

  @Test
  void findById_shouldReturnProduct_whenIdExists() {
    mockFindById(id, existingProduct);

    ProductResponseDTO result = productService.findById(id);

    assertNotNull(result);
    assertEquals(existingProduct.getCode(), result.code);
  }

  @Test
  void findById_shouldThrowResourceNotFoundException_whenIdNotFound() {
    mockFindById(id, null);

    assertThrows(ResourceNotFoundException.class, () -> productService.findById(id));
  }

  @Test
  void findAll_shouldReturnPagedResults_withAscendingOrder() {
    PageRequestDTO pageRequest = new PageRequestDTO(0, 10, "name", "asc");

    List<Product> products =
        List.of(
            ProductFactory.createProductWithCode("PROD001"),
            ProductFactory.createProductWithCode("PROD002"),
            ProductFactory.createProductWithCode("PROD003"));

    mockFindAll(products, 3L, 1);

    PageResponseDTO<ProductResponseDTO> result = productService.findAll(pageRequest);

    assertNotNull(result);
    assertEquals(3, result.content.size());
    assertEquals(3L, result.totalElements);
    assertEquals(0, result.page);
    assertEquals(10, result.size);
  }

  @Test
  void findAll_shouldReturnPagedResults_withDescendingOrder() {
    PageRequestDTO pageRequest = new PageRequestDTO(0, 10, "code", "desc");
    List<Product> products = List.of(ProductFactory.createProductWithCode("PROD003"));
    mockFindAll(products, 1L, 1);

    PageResponseDTO<ProductResponseDTO> result = productService.findAll(pageRequest);

    assertNotNull(result);
    assertEquals(1, result.content.size());
    assertEquals("PROD003", result.content.get(0).code);
  }

  @Test
  void findAll_shouldReturnEmptyPage_whenNoResults() {
    PageRequestDTO pageRequest = new PageRequestDTO(0, 10, "name", "asc");
    mockFindAll(List.of(), 0L, 0);

    PageResponseDTO<ProductResponseDTO> result = productService.findAll(pageRequest);

    assertNotNull(result);
    assertEquals(0, result.content.size());
    assertEquals(0L, result.totalElements);
  }

  @Test
  void findAll_shouldHandlePagination_withMultiplePages() {
    PageRequestDTO pageRequest = new PageRequestDTO(1, 2, "name", "asc");

    List<Product> products =
        List.of(
            ProductFactory.createProductWithCode("PROD003"),
            ProductFactory.createProductWithCode("PROD004"));

    mockFindAll(products, 5L, 3);

    PageResponseDTO<ProductResponseDTO> result = productService.findAll(pageRequest);

    assertNotNull(result);
    assertEquals(2, result.content.size());
    assertEquals(1, result.page);
    assertEquals(2, result.size);
    assertEquals(5L, result.totalElements);
    assertEquals(3, result.totalPages);
  }

  @Test
  void findByName_shouldReturnPagedResults_whenNameProvided() {
    PageRequestDTO pageRequest = new PageRequestDTO(1, 2, "name", "asc");

    List<Product> products =
        List.of(
            new Product("P-1", "Steel Table", dto.price),
            new Product("P-2", "Steel Chair", dto.price));
    mockFindByName(products, 4L, 2);

    PageResponseDTO<ProductResponseDTO> result = productService.findByName("steel", pageRequest);

    assertNotNull(result);
    assertEquals(2, result.content.size());
    assertEquals(4L, result.totalElements);
    assertEquals(2, result.totalPages);
    assertEquals(1, result.page);
    assertEquals(2, result.size);
  }

  @Test
  void findByName_shouldReturnPagedResults_whenNameProvidedWithDescendingOrder() {
    PageRequestDTO pageRequest = new PageRequestDTO(0, 5, "code", "desc");

    List<Product> products = List.of(ProductFactory.createProductWithCode("PROD999"));
    mockFindByName(products, 1L, 1);

    PageResponseDTO<ProductResponseDTO> result = productService.findByName("prod", pageRequest);

    assertNotNull(result);
    assertEquals(1, result.content.size());
    assertEquals("PROD999", result.content.get(0).code);
    assertEquals(1L, result.totalElements);
    assertEquals(1, result.totalPages);
    assertEquals(0, result.page);
    assertEquals(5, result.size);
  }

  @Test
  void findByName_shouldNormalizeDefaults_whenNameProvidedAndPagingInvalid() {
    PageRequestDTO pageRequest = new PageRequestDTO(-10, 0, "   ", "   ");

    List<Product> products =
        List.of(
            new Product("P-1", "Steel Table", dto.price),
            new Product("P-2", "Steel Chair", dto.price));
    mockFindByName(products, 2L, 1);

    PageResponseDTO<ProductResponseDTO> result = productService.findByName("steel", pageRequest);

    assertNotNull(result);
    assertEquals(2, result.content.size());
    assertEquals(2L, result.totalElements);
    assertEquals(1, result.totalPages);
    assertEquals(0, result.page);
    assertEquals(10, result.size);
  }

  @Test
  void findByName_shouldReturnEmptyPage_whenNameIsBlank() {
    PageRequestDTO pageRequest = new PageRequestDTO(1, 2, "name", "asc");

    PageResponseDTO<ProductResponseDTO> result = productService.findByName("   ", pageRequest);

    assertNotNull(result);
    assertTrue(result.content.isEmpty());
    assertEquals(0L, result.totalElements);
    assertEquals(0, result.totalPages);
    assertEquals(1, result.page);
    assertEquals(2, result.size);
  }

  @Test
  void findByName_shouldReturnEmptyPage_whenNameIsNull_andNormalizePageAndSize() {
    PageRequestDTO pageRequest = new PageRequestDTO(-10, 0, null, null);

    PageResponseDTO<ProductResponseDTO> result = productService.findByName(null, pageRequest);

    assertNotNull(result);
    assertTrue(result.content.isEmpty());
    assertEquals(0L, result.totalElements);
    assertEquals(0, result.totalPages);
    assertEquals(0, result.page);
    assertEquals(10, result.size);
  }
}
