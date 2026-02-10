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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@QuarkusTest
public class RawMaterialServiceTest {

    @Inject
    RawMaterialService rawMaterialService;

    @InjectMock
    EntityManager entityManager;

    private RawMaterialRequestDTO dto;
    private RawMaterial existingRawMaterial;
    private Long id;

    @BeforeEach
    void setup() {
        id = 1L;
        dto = RawMaterialFactory.createRawMaterialRequestDTO();
        existingRawMaterial = RawMaterialFactory.createRawMaterialWithCode(dto.code);

        PanacheMock.mock(RawMaterial.class);
        when(RawMaterial.getEntityManager()).thenReturn(entityManager);
    }

    private void mockFindByCode(RawMaterial result) {
        PanacheQuery<RawMaterial> mockQuery = mock(PanacheQuery.class);
        doReturn(Optional.ofNullable(result)).when(mockQuery).firstResultOptional();
        doReturn(result).when(mockQuery).firstResult();
        PanacheMock.doReturn(mockQuery).when(RawMaterial.class).find(eq("code"), any(Object[].class));
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
        doNothing().when(entityManager).persist(any(RawMaterial.class));

        RawMaterialResponseDTO result = rawMaterialService.insert(dto);

        assertRawMaterialEquals(result, dto);
        verify(entityManager, times(1)).persist(any(RawMaterial.class));
    }

    @Test
    void insert_shouldThrowConflictException_whenCodeAlreadyExists() {
        mockFindByCode(existingRawMaterial);

        assertThrows(ConflictException.class, () -> rawMaterialService.insert(dto));
        verify(entityManager, never()).persist(any());
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
        when(PanacheMock.getMock(RawMaterial.class).deleteById(id)).thenReturn(true);

        assertDoesNotThrow(() -> rawMaterialService.delete(id));
        verify(entityManager, times(1)).flush();
    }

    @Test
    void delete_shouldThrowResourceNotFoundException_whenIdNotFound() {
        mockFindById(id, null);

        assertThrows(ResourceNotFoundException.class,
                () -> rawMaterialService.delete(id));
    }

    @Test
    void delete_shouldThrowDatabaseException_whenEntityIsReferenced() {
        existingRawMaterial.setId(id);
        mockFindById(id, existingRawMaterial);

        doThrow(new PersistenceException("Constraint violation"))
                .when(PanacheMock.getMock(RawMaterial.class))
                .deleteById(id);

        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> rawMaterialService.delete(id));

        assertEquals("Cannot delete raw material because it is referenced by other records",
                exception.getMessage());
        verify(entityManager, never()).flush();
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

    private void mockFindByName(List<RawMaterial> results, long count, int pageCount) {
        PanacheQuery<RawMaterial> mockQuery = mockPanacheQuery(results, count, pageCount);
        PanacheMock.doReturn(mockQuery).when(RawMaterial.class).find(
                eq("lower(name) like concat('%', lower(?1), '%')"),
                any(Sort.class),
                any(Object[].class)
        );
    }

    @Test
    void findByName_shouldReturnMatches_whenNameFragmentProvided() {
        PageRequestDTO pageRequest = new PageRequestDTO(0, 10, "name", "asc");

        RawMaterial m1 = new RawMaterial("RM-1", "Steel Sheet", dto.stockQuantity);
        RawMaterial m2 = new RawMaterial("RM-2", "Stainless Steel", dto.stockQuantity);
        RawMaterial m3 = new RawMaterial("RM-3", "Aluminum", dto.stockQuantity);

        mockFindByName(List.of(m1, m2), 2L, 1);

        PageResponseDTO<RawMaterialResponseDTO> result = rawMaterialService.findByName("steel", pageRequest);

        assertNotNull(result);
        assertEquals(2, result.content.size());
        assertEquals(2L, result.totalElements);
        assertEquals(1, result.totalPages);
        assertEquals("RM-1", result.content.get(0).code);
        assertEquals("RM-2", result.content.get(1).code);
    }

    @Test
    void findByName_shouldBeCaseInsensitive() {
        PageRequestDTO pageRequest = new PageRequestDTO(0, 10, "name", "asc");

        RawMaterial m1 = new RawMaterial("RM-1", "Steel Sheet", dto.stockQuantity);
        mockFindByName(List.of(m1), 1L, 1);

        PageResponseDTO<RawMaterialResponseDTO> result = rawMaterialService.findByName("StEeL", pageRequest);

        assertEquals(1, result.content.size());
        assertEquals("RM-1", result.content.getFirst().code);
    }

    @Test
    void findByName_shouldReturnPagedResults_whenNameProvidedWithDescendingOrder() {
        PageRequestDTO pageRequest = new PageRequestDTO(0, 5, "code", "desc");

        RawMaterial m1 = RawMaterialFactory.createRawMaterialWithCode("RM-999");
        mockFindByName(List.of(m1), 1L, 1);

        PageResponseDTO<RawMaterialResponseDTO> result = rawMaterialService.findByName("rm", pageRequest);

        assertNotNull(result);
        assertEquals(1, result.content.size());
        assertEquals("RM-999", result.content.get(0).code);
        assertEquals(1L, result.totalElements);
        assertEquals(1, result.totalPages);
        assertEquals(0, result.page);
        assertEquals(5, result.size);
    }

    @Test
    void findByName_shouldNormalizeDefaults_whenNameProvidedAndPagingInvalid() {
        PageRequestDTO pageRequest = new PageRequestDTO(-10, 0, "   ", "   ");

        RawMaterial m1 = new RawMaterial("RM-1", "Steel Sheet", dto.stockQuantity);
        RawMaterial m2 = new RawMaterial("RM-2", "Steel Rod", dto.stockQuantity);
        mockFindByName(List.of(m1, m2), 2L, 1);

        PageResponseDTO<RawMaterialResponseDTO> result = rawMaterialService.findByName("steel", pageRequest);

        assertNotNull(result);
        assertEquals(2, result.content.size());
        assertEquals(2L, result.totalElements);
        assertEquals(1, result.totalPages);
        assertEquals(0, result.page);
        assertEquals(10, result.size);
    }

    @Test
    void findByName_shouldReturnEmptyList_whenNameIsBlank() {
        PageRequestDTO pageRequest = new PageRequestDTO(1, 5, "name", "asc");
        PageResponseDTO<RawMaterialResponseDTO> result = rawMaterialService.findByName("   ", pageRequest);

        assertNotNull(result);
        assertTrue(result.content.isEmpty());
        assertEquals(0L, result.totalElements);
        assertEquals(0, result.totalPages);
        assertEquals(1, result.page);
        assertEquals(5, result.size);
    }

    @Test
    void findByName_shouldReturnEmptyPage_whenNameIsNull_andNormalizePageAndSize() {
        PageRequestDTO pageRequest = new PageRequestDTO(-1, 0, null, null);

        PageResponseDTO<RawMaterialResponseDTO> result = rawMaterialService.findByName(null, pageRequest);

        assertNotNull(result);
        assertTrue(result.content.isEmpty());
        assertEquals(0L, result.totalElements);
        assertEquals(0, result.totalPages);
        assertEquals(0, result.page);
        assertEquals(10, result.size);
    }
}
