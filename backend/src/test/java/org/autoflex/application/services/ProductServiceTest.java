package org.autoflex.application.services;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.autoflex.domain.entities.Product;
import org.autoflex.domain.entities.ProductRawMaterial;
import org.autoflex.domain.entities.RawMaterial;
import org.autoflex.factory.ProductFactory;
import org.autoflex.factory.RawMaterialFactory;
import org.autoflex.web.dto.PageRequestDTO;
import org.autoflex.web.dto.PageResponseDTO;
import org.autoflex.web.dto.ProductRequestDTO;
import org.autoflex.web.dto.ProductResponseDTO;
import org.autoflex.web.exceptions.ConflictException;
import org.autoflex.web.exceptions.DatabaseException;
import org.autoflex.web.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ProductServiceTest {

    @Inject
    ProductService productService;

    @InjectMock
    EntityManager entityManager;

    private ProductRequestDTO dto;
    private ProductRequestDTO dtoWithRawMaterials;
    private Product existingProduct;
    private RawMaterial rawMaterial1;
    private RawMaterial rawMaterial2;
    private Long id;

    @BeforeEach
    void setup() {
        id = 1L;
        dto = ProductFactory.createProductRequestDTO();
        dtoWithRawMaterials = ProductFactory.createProductRequestDTOWithRawMaterials();
        existingProduct = ProductFactory.createProductWithCode(dto.code);

        rawMaterial1 = RawMaterialFactory.createRawMaterialWithCode("RAW001");
        rawMaterial1.setId(1L);
        rawMaterial2 = RawMaterialFactory.createRawMaterialWithCode("RAW002");
        rawMaterial2.setId(2L);

        PanacheMock.mock(Product.class);
        PanacheMock.mock(RawMaterial.class);
        PanacheMock.mock(ProductRawMaterial.class);
    }

    private void mockFindByCode(Product result) {
        PanacheQuery mockQuery = mock(PanacheQuery.class);
        when(mockQuery.firstResult()).thenReturn(result);
        when(mockQuery.firstResultOptional()).thenReturn(Optional.ofNullable(result));
        when(Product.find("code", dto.code)).thenReturn(mockQuery);
    }

    private void mockFindById(Long id, Product result) {
        when(Product.findById(id)).thenReturn(result);
    }

    private void mockRawMaterialFindById(Long rawMaterialId, RawMaterial rawMaterial) {
        when(RawMaterial.findByIdOptional(rawMaterialId))
                .thenReturn(Optional.ofNullable(rawMaterial));
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

    private void assertProductEquals(ProductResponseDTO result, ProductRequestDTO expected) {
        assertNotNull(result);
        assertEquals(expected.code, result.code);
        assertEquals(expected.name, result.name);
        assertEquals(expected.price, result.price);
    }

    @Test
    void insert_shouldCreateProduct_whenValidRequestWithoutRawMaterials() {
        mockFindByCode(null);

        doNothing().when(entityManager).persist(any(Product.class));

        ProductResponseDTO result = productService.insert(dto);

        assertProductEquals(result, dto);
        verify(entityManager, times(1)).persist(any(Product.class));
    }

    @Test
    void insert_shouldCreateProduct_whenValidRequestWithRawMaterials() {
        mockFindByCode(null);
        mockRawMaterialFindById(1L, rawMaterial1);
        mockRawMaterialFindById(2L, rawMaterial2);

        doNothing().when(entityManager).persist(any(Product.class));

        ProductResponseDTO result = productService.insert(dtoWithRawMaterials);

        assertProductEquals(result, dtoWithRawMaterials);
        assertEquals(2, result.rawMaterials.size());

        verify(entityManager, times(1)).persist(any(Product.class));
    }

    @Test
    void insert_shouldThrowConflictException_whenCodeAlreadyExists() {
        PanacheQuery mockQuery = mock(PanacheQuery.class);
        when(mockQuery.firstResultOptional()).thenReturn(Optional.of(existingProduct));
        when(Product.find("code", dto.code)).thenReturn(mockQuery);

        assertThrows(ConflictException.class, () -> productService.insert(dto));

        verify(entityManager, never()).persist(any());
    }

    @Test
    void insert_shouldThrowResourceNotFoundException_whenRawMaterialNotFound() {
        mockFindByCode(null);
        mockRawMaterialFindById(1L, null);

        assertThrows(ResourceNotFoundException.class,
                () -> productService.insert(dtoWithRawMaterials));
    }

    @Test
    void update_shouldUpdateProduct_whenValidRequest() {
        existingProduct.setId(id);
        mockFindById(id, existingProduct);
        mockFindByCode(null); // Nenhum outro produto com esse código
        mockRawMaterialFindById(1L, rawMaterial1);
        mockRawMaterialFindById(2L, rawMaterial2);

        ProductResponseDTO result = productService.update(id, dtoWithRawMaterials);

        assertProductEquals(result, dtoWithRawMaterials);
    }

    @Test
    void update_shouldThrowResourceNotFoundException_whenIdNotFound() {
        mockFindById(id, null);

        assertThrows(ResourceNotFoundException.class,
                () -> productService.update(id, dto));
    }

    @Test
    void update_shouldThrowConflictException_whenCodeExistsInOtherEntity() {
        existingProduct.setId(id);
        Product otherProduct = ProductFactory.createProductWithCode(dto.code);
        otherProduct.setId(2L);

        mockFindById(id, existingProduct);
        mockFindByCode(otherProduct);

        assertThrows(ConflictException.class,
                () -> productService.update(id, dto));
    }

    @Test
    void update_shouldAllowSameCode_whenUpdatingSameEntity() {
        existingProduct.setId(id);
        mockFindById(id, existingProduct);
        mockFindByCode(existingProduct);

        ProductResponseDTO result = productService.update(id, dto);

        assertNotNull(result);
        assertEquals(dto.code, result.code);
    }

    @Test
    void update_shouldThrowResourceNotFoundException_whenRawMaterialNotFound() {
        existingProduct.setId(id);
        mockFindById(id, existingProduct);
        mockFindByCode(null);
        mockRawMaterialFindById(1L, null);

        assertThrows(ResourceNotFoundException.class,
                () -> productService.update(id, dtoWithRawMaterials));
    }

    @Test
    void delete_shouldDeleteProduct_whenValidId() {
        existingProduct.setId(id);
        mockFindById(id, existingProduct);

        Mockito.when(PanacheMock.getMock(Product.class).deleteById(id)).thenReturn(true);

        assertDoesNotThrow(() -> productService.delete(id));
    }

    @Test
    void delete_shouldThrowResourceNotFoundException_whenIdNotFound() {
        mockFindById(id, null);

        assertThrows(ResourceNotFoundException.class,
                () -> productService.delete(id));
    }

    @Test
    void delete_shouldThrowDatabaseException_whenEntityIsReferenced() {
        existingProduct.setId(id);
        mockFindById(id, existingProduct);

        Mockito.doThrow(new PersistenceException("Constraint violation"))
                .when(PanacheMock.getMock(Product.class))
                .deleteById(id);

        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> productService.delete(id));

        assertEquals("Cannot delete product because it is referenced by other records",
                exception.getMessage());
    }

    @Test
    void findById_shouldReturnProduct_whenIdExists() {
        existingProduct.setId(id);
        mockFindById(id, existingProduct);

        ProductResponseDTO result = productService.findById(id);

        assertNotNull(result);
        assertEquals(existingProduct.getCode(), result.code);
    }

    @Test
    void findById_shouldThrowResourceNotFoundException_whenIdNotFound() {
        mockFindById(id, null);

        assertThrows(ResourceNotFoundException.class,
                () -> productService.findById(id));
    }

    @Test
    void findAll_shouldReturnPagedResults_withAscendingOrder() {
        PageRequestDTO pageRequest = new PageRequestDTO(0, 10, "name", "asc");

        List<Product> products = List.of(
                ProductFactory.createProductWithCode("PROD001"),
                ProductFactory.createProductWithCode("PROD002"),
                ProductFactory.createProductWithCode("PROD003")
        );

        mockFindAll(products, 3L, 1);

        PageResponseDTO<ProductResponseDTO> result = productService.findAll(pageRequest);

        assertNotNull(result);
        assertEquals(3, result.content.size());
        assertEquals(3L, result.totalElements);
    }

    @Test
    void findAll_shouldReturnPagedResults_withDescendingOrder() {
        PageRequestDTO pageRequest = new PageRequestDTO(0, 10, "code", "desc");
        List<Product> products = List.of(ProductFactory.createProductWithCode("PROD003"));
        mockFindAll(products, 1L, 1);

        PageResponseDTO<ProductResponseDTO> result = productService.findAll(pageRequest);
        assertNotNull(result);
        assertEquals("PROD003", result.content.get(0).code);
    }

    @Test
    void findAll_shouldReturnEmptyPage_whenNoResults() {
        PageRequestDTO pageRequest = new PageRequestDTO(0, 10, "name", "asc");
        mockFindAll(List.of(), 0L, 0);

        PageResponseDTO<ProductResponseDTO> result = productService.findAll(pageRequest);

        assertEquals(0, result.content.size());
    }

    @Test
    void findAll_shouldHandlePagination_withMultiplePages() {
        PageRequestDTO pageRequest = new PageRequestDTO(1, 2, "name", "asc");
        List<Product> products = List.of(ProductFactory.createProductWithCode("PROD003"), ProductFactory.createProductWithCode("PROD004"));
        mockFindAll(products, 5L, 3);

        PageResponseDTO<ProductResponseDTO> result = productService.findAll(pageRequest);

        assertEquals(2, result.content.size());
        assertEquals(1, result.page);
    }
}