package org.autoflex.application.services;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import org.autoflex.domain.entities.RawMaterial;
import org.autoflex.factory.RawMaterialFactory;
import org.autoflex.web.dto.PageRequestDTO;
import org.autoflex.web.dto.PageResponseDTO;
import org.autoflex.web.dto.RawMaterialRequestDTO;
import org.autoflex.web.dto.RawMaterialResponseDTO;
import org.autoflex.web.exceptions.ConflictException;
import org.autoflex.web.exceptions.DatabaseException;
import org.autoflex.web.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class RawMaterialServiceTest {

    @Inject
    RawMaterialService rawMaterialService;

    private RawMaterialRequestDTO dto;
    private RawMaterial existingRawMaterial;
    private Long id;

    @BeforeEach
    void setup() {
        id = 1L;
        dto = RawMaterialFactory.createRawMaterialRequestDTO();
        existingRawMaterial = RawMaterialFactory.createRawMaterialWithCode(dto.code);
        PanacheMock.mock(RawMaterial.class);
    }

    private void mockFindByCode(RawMaterial result) {
        PanacheQuery mockQuery = mock(PanacheQuery.class);
        when(mockQuery.firstResultOptional()).thenReturn(Optional.ofNullable(result));
        when(mockQuery.firstResult()).thenReturn(result);
        when(RawMaterial.find("code", dto.code)).thenReturn(mockQuery);
    }

    private void mockFindById(Long id, RawMaterial result) {
        when(RawMaterial.findById(id)).thenReturn(result);
    }

    private PanacheQuery<RawMaterial> mockPanacheQuery(List<RawMaterial> items, long count, int pageCount) {
        PanacheQuery<RawMaterial> mockQuery = mock(PanacheQuery.class);

        doReturn(mockQuery).when(mockQuery).page(any(Page.class));
        when(mockQuery.list()).thenReturn(items);
        when(mockQuery.count()).thenReturn(count);
        when(mockQuery.pageCount()).thenReturn(pageCount);

        return mockQuery;
    }

    private void mockFindAll(List<RawMaterial> materials, long count, int pageCount) {
        PanacheQuery<RawMaterial> mockQuery = mockPanacheQuery(materials, count, pageCount);
        PanacheMock.doReturn(mockQuery).when(RawMaterial.class).findAll(any(Sort.class));
    }

    private void assertRawMaterialEquals(RawMaterialResponseDTO result, RawMaterialRequestDTO expected) {
        assertNotNull(result);
        assertEquals(expected.code, result.code);
        assertEquals(expected.name, result.name);
        assertEquals(expected.stockQuantity, result.stockQuantity);
    }

    @Test
    void insert_shouldCreateRawMaterial_whenValidRequest() {
        mockFindByCode(null);
        doNothing().when(PanacheMock.getMock(RawMaterial.class)).persist();

        RawMaterialResponseDTO result = rawMaterialService.insert(dto);

        assertRawMaterialEquals(result, dto);
    }

    @Test
    void insert_shouldThrowConflictException_whenCodeAlreadyExists() {
        mockFindByCode(existingRawMaterial);

        assertThrows(ConflictException.class, () -> rawMaterialService.insert(dto));
    }

    @Test
    void update_shouldUpdateRawMaterial_whenValidRequest() {
        existingRawMaterial.setId(id);
        mockFindById(id, existingRawMaterial);
        mockFindByCode(null);

        RawMaterialResponseDTO result = rawMaterialService.update(id, dto);

        assertRawMaterialEquals(result, dto);

        assertEquals(dto.code, existingRawMaterial.getCode());
        assertEquals(dto.name, existingRawMaterial.getName());
        assertEquals(dto.stockQuantity, existingRawMaterial.getStockQuantity());
    }

    @Test
    void update_shouldThrowResourceNotFoundException_whenIdNotFound() {
        mockFindById(id, null);

        assertThrows(ResourceNotFoundException.class,
                () -> rawMaterialService.update(id, dto));
    }

    @Test
    void update_shouldThrowConflictException_whenCodeExistsInOtherEntity() {
        existingRawMaterial.setId(id);

        RawMaterial otherEntity = RawMaterialFactory.createRawMaterialWithCode(dto.code);
        otherEntity.setId(2L);

        mockFindById(id, existingRawMaterial);
        mockFindByCode(otherEntity);

        assertThrows(ConflictException.class,
                () -> rawMaterialService.update(id, dto));
    }

    @Test
    void update_shouldAllowSameCode_whenUpdatingSameEntity() {
        existingRawMaterial.setId(id);

        mockFindById(id, existingRawMaterial);
        mockFindByCode(existingRawMaterial);

        RawMaterialResponseDTO result = rawMaterialService.update(id, dto);

        assertNotNull(result);
        assertEquals(dto.code, result.code);
    }

    @Test
    void delete_shouldDeleteRawMaterial_whenValidId() {
        existingRawMaterial.setId(id);
        mockFindById(id, existingRawMaterial);

        assertDoesNotThrow(() -> rawMaterialService.delete(id));
    }

    @Test
    void delete_shouldThrowResourceNotFoundException_whenIdNotFound() {
        mockFindById(id, null);

        assertThrows(ResourceNotFoundException.class,
                () -> rawMaterialService.delete(id));
    }

    @Test
    @SuppressWarnings("static-access")
    void delete_shouldThrowDatabaseException_whenEntityIsReferenced() {
        existingRawMaterial.setId(id);
        mockFindById(id, existingRawMaterial);

        Mockito.doThrow(new PersistenceException("Constraint violation"))
                .when(PanacheMock.getMock(RawMaterial.class))
                .deleteById(id);

        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> rawMaterialService.delete(id));

        assertEquals("Cannot delete raw material because it is referenced by other records",
                exception.getMessage());
    }

    @Test
    void findById_shouldReturnRawMaterial_whenIdExists() {
        existingRawMaterial.setId(id);
        mockFindById(id, existingRawMaterial);

        RawMaterialResponseDTO result = rawMaterialService.findById(id);

        assertNotNull(result);
        assertEquals(existingRawMaterial.getCode(), result.code);
        assertEquals(existingRawMaterial.getName(), result.name);
        assertEquals(existingRawMaterial.getStockQuantity(), result.stockQuantity);

    }

    @Test
    void findById_shouldThrowResourceNotFoundException_whenIdNotFound() {
        mockFindById(id, null);

        assertThrows(ResourceNotFoundException.class,
                () -> rawMaterialService.findById(id));
    }

    @Test
    void findAll_shouldReturnPagedResults_withAscendingOrder() {
        PageRequestDTO pageRequest = new PageRequestDTO(0, 10, "name", "asc");

        List<RawMaterial> materials = List.of(
                RawMaterialFactory.createRawMaterialWithCode("CODE1"),
                RawMaterialFactory.createRawMaterialWithCode("CODE2"),
                RawMaterialFactory.createRawMaterialWithCode("CODE3")
        );

        mockFindAll(materials, 3L, 1);

        PageResponseDTO<RawMaterialResponseDTO> result = rawMaterialService.findAll(pageRequest);

        assertNotNull(result);
        assertEquals(3, result.content.size());
        assertEquals(3L, result.totalElements);
        assertEquals(1, result.totalPages);
        assertEquals(0, result.page);
        assertEquals(10, result.size);
    }

    @Test
    void findAll_shouldReturnPagedResults_withDescendingOrder() {
        PageRequestDTO pageRequest = new PageRequestDTO(0, 10, "code", "desc");

        List<RawMaterial> materials = List.of(
                RawMaterialFactory.createRawMaterialWithCode("CODE3"),
                RawMaterialFactory.createRawMaterialWithCode("CODE2"),
                RawMaterialFactory.createRawMaterialWithCode("CODE1")
        );

        mockFindAll(materials, 3L, 1);

        PageResponseDTO<RawMaterialResponseDTO> result = rawMaterialService.findAll(pageRequest);

        assertNotNull(result);
        assertEquals(3, result.content.size());
        assertEquals("CODE3", result.content.get(0).code);
    }

    @Test
    void findAll_shouldReturnEmptyPage_whenNoResults() {
        PageRequestDTO pageRequest = new PageRequestDTO(0, 10, "name", "asc");

        mockFindAll(List.of(), 0L, 0);

        PageResponseDTO<RawMaterialResponseDTO> result = rawMaterialService.findAll(pageRequest);

        assertNotNull(result);
        assertEquals(0, result.content.size());
        assertEquals(0L, result.totalElements);
        assertEquals(0, result.totalPages);
    }

    @Test
    void findAll_shouldHandlePagination_withMultiplePages() {
        PageRequestDTO pageRequest = new PageRequestDTO(1, 2, "name", "asc");

        List<RawMaterial> materials = List.of(
                RawMaterialFactory.createRawMaterialWithCode("CODE3"),
                RawMaterialFactory.createRawMaterialWithCode("CODE4")
        );

        mockFindAll(materials, 5L, 3);

        PageResponseDTO<RawMaterialResponseDTO> result = rawMaterialService.findAll(pageRequest);

        assertNotNull(result);
        assertEquals(2, result.content.size());
        assertEquals(5L, result.totalElements);
        assertEquals(3, result.totalPages);
        assertEquals(1, result.page);
        assertEquals(2, result.size);
    }
}