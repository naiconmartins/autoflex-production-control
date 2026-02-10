package org.autoflex.application.services;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.autoflex.domain.entities.Product;
import org.autoflex.domain.entities.ProductRawMaterial;
import org.autoflex.domain.entities.RawMaterial;
import org.autoflex.factory.ProductRawMaterialFactory;
import org.autoflex.web.dto.ProductRawMaterialRequestDTO;
import org.autoflex.web.dto.ProductRawMaterialResponseDTO;
import org.autoflex.web.exceptions.ConflictException;
import org.autoflex.web.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ProductRawMaterialServiceTest {

    @Inject
    ProductRawMaterialService service;

    @InjectMock
    EntityManager entityManager;

    private Long productId;
    private Long rawMaterialId;
    private Product product;
    private RawMaterial rawMaterial;
    private ProductRawMaterialRequestDTO requestDto;

    @BeforeEach
    void setup() {
        productId = 1L;
        rawMaterialId = 2L;

        product = ProductRawMaterialFactory.createProduct(productId, "P001");
        rawMaterial = ProductRawMaterialFactory.createRawMaterial(rawMaterialId, "R001");
        requestDto = ProductRawMaterialFactory.createRequest(rawMaterialId, new BigDecimal("10.0"));

        PanacheMock.mock(Product.class);
        PanacheMock.mock(RawMaterial.class);
        PanacheMock.mock(ProductRawMaterial.class);

        when(Product.getEntityManager()).thenReturn(entityManager);
        when(RawMaterial.getEntityManager()).thenReturn(entityManager);
        when(ProductRawMaterial.getEntityManager()).thenReturn(entityManager);
    }

    private void mockFindLink(ProductRawMaterial result) {
        PanacheQuery query = mock(PanacheQuery.class);
        when(query.firstResultOptional()).thenReturn(Optional.ofNullable(result));
        when(query.firstResult()).thenReturn(result);
        when(ProductRawMaterial.find(anyString(), (Object[]) any())).thenReturn(query);
    }

    @Test
    void add_shouldCreateLink_whenValidRequest() {
        when(Product.findById(productId)).thenReturn(product);
        when(RawMaterial.findById(rawMaterialId)).thenReturn(rawMaterial);
        mockFindLink(null);
        doNothing().when(entityManager).persist(any());

        ProductRawMaterialResponseDTO result = service.add(productId, requestDto);

        assertNotNull(result);
        assertEquals(rawMaterialId, result.id);
        verify(entityManager, times(1)).persist(any(ProductRawMaterial.class));
    }

    @Test
    void add_shouldThrowConflictException_whenLinkAlreadyExists() {
        when(Product.findById(productId)).thenReturn(product);
        when(RawMaterial.findById(rawMaterialId)).thenReturn(rawMaterial);

        ProductRawMaterial existingLink = ProductRawMaterialFactory.createLink(product, rawMaterial, BigDecimal.ONE);
        mockFindLink(existingLink);

        assertThrows(ConflictException.class, () -> service.add(productId, requestDto));
        verify(entityManager, never()).persist(any());
    }

    @Test
    void listByProduct_shouldReturnList_whenProductExists() {
        when(Product.findById(productId)).thenReturn(product);

        ProductRawMaterial link = ProductRawMaterialFactory.createLink(product, rawMaterial, BigDecimal.TEN);
        PanacheQuery query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(List.of(link));
        when(ProductRawMaterial.find("product", product)).thenReturn(query);

        List<ProductRawMaterialResponseDTO> result = service.listByProduct(productId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void updateRequiredQuantity_shouldUpdate_whenLinkExists() {
        when(Product.findById(productId)).thenReturn(product);
        when(RawMaterial.findById(rawMaterialId)).thenReturn(rawMaterial);

        ProductRawMaterial existingLink = ProductRawMaterialFactory.createLink(product, rawMaterial, BigDecimal.ONE);
        mockFindLink(existingLink);

        ProductRawMaterialResponseDTO result = service.updateRequiredQuantity(productId, rawMaterialId, requestDto);

        assertNotNull(result);
        assertEquals(requestDto.requiredQuantity, result.requiredQuantity);
    }

    @Test
    void remove_shouldDelete_whenLinkExists() {
        when(Product.findById(productId)).thenReturn(product);
        when(RawMaterial.findById(rawMaterialId)).thenReturn(rawMaterial);

        ProductRawMaterial existingLink = mock(ProductRawMaterial.class);
        mockFindLink(existingLink);

        assertDoesNotThrow(() -> service.remove(productId, rawMaterialId));
        verify(existingLink, times(1)).delete();
    }

    @Test
    void add_shouldThrowResourceNotFoundException_whenProductNotFound() {
        when(Product.findById(productId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.add(productId, requestDto));
    }

    @Test
    void add_shouldThrowResourceNotFoundException_whenRawMaterialNotFound() {
        when(Product.findById(productId)).thenReturn(product);
        when(RawMaterial.findById(rawMaterialId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.add(productId, requestDto));
    }

    @Test
    void listByProduct_shouldThrowResourceNotFoundException_whenProductNotFound() {
        when(Product.findById(productId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.listByProduct(productId));
    }

    @Test
    void updateRequiredQuantity_shouldThrowResourceNotFoundException_whenLinkDoesNotExist() {
        when(Product.findById(productId)).thenReturn(product);
        when(RawMaterial.findById(rawMaterialId)).thenReturn(rawMaterial);
        mockFindLink(null);

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateRequiredQuantity(productId, rawMaterialId, requestDto));
    }

    @Test
    void remove_shouldThrowResourceNotFoundException_whenLinkDoesNotExist() {
        when(Product.findById(productId)).thenReturn(product);
        when(RawMaterial.findById(rawMaterialId)).thenReturn(rawMaterial);
        mockFindLink(null);

        assertThrows(ResourceNotFoundException.class, () -> service.remove(productId, rawMaterialId));
    }

    @Test
    void updateRequiredQuantity_shouldThrowResourceNotFoundException_whenProductNotFound() {
        when(Product.findById(productId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateRequiredQuantity(productId, rawMaterialId, requestDto));
    }

    @Test
    void updateRequiredQuantity_shouldThrowResourceNotFoundException_whenRawMaterialNotFound() {
        when(Product.findById(productId)).thenReturn(product);
        when(RawMaterial.findById(rawMaterialId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateRequiredQuantity(productId, rawMaterialId, requestDto));
    }

    @Test
    void remove_shouldThrowResourceNotFoundException_whenProductNotFound() {
        when(Product.findById(productId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.remove(productId, rawMaterialId));
    }

    @Test
    void remove_shouldThrowResourceNotFoundException_whenRawMaterialNotFound() {
        when(Product.findById(productId)).thenReturn(product);
        when(RawMaterial.findById(rawMaterialId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.remove(productId, rawMaterialId));
    }
}
