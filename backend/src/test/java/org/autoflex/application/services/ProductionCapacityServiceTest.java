package org.autoflex.application.services;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.autoflex.domain.entities.Product;
import org.autoflex.domain.entities.ProductRawMaterial;
import org.autoflex.domain.entities.RawMaterial;
import org.autoflex.factory.ProductFactory;
import org.autoflex.factory.ProductionCapacityFactory;
import org.autoflex.factory.RawMaterialFactory;
import org.autoflex.web.dto.ProductionPlanResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ProductionCapacityServiceTest {

    @Inject
    ProductionCapacityService service;

    @InjectMock
    EntityManager entityManager;

    private RawMaterial wood;
    private Product table;
    private Product chair;

    @BeforeEach
    void setup() {
        PanacheMock.mock(Product.class);
        PanacheMock.mock(RawMaterial.class);
        PanacheMock.mock(ProductRawMaterial.class);

        when(Product.getEntityManager()).thenReturn(entityManager);
        when(RawMaterial.getEntityManager()).thenReturn(entityManager);
        when(ProductRawMaterial.getEntityManager()).thenReturn(entityManager);

        wood = RawMaterialFactory.createRawMaterialWithCode("WOOD");
        wood.setId(1L);
        wood.setStockQuantity(new BigDecimal("100.0"));

        table = ProductFactory.createProductWithCode("TAB01");
        table.setId(10L);

        chair = ProductFactory.createProductWithCode("CHA01");
        chair.setId(11L);
    }

    private void mockRecipe(Product product, List<ProductRawMaterial> recipe) {
        PanacheQuery query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(recipe);
        when(ProductRawMaterial.find("product", product)).thenReturn(query);
    }

    @Test
    void generate_shouldIncludeMultipleItems_whenStockIsSufficientForBoth() {
        when(RawMaterial.listAll()).thenReturn(List.of(wood));
        when(Product.listAll(any(Sort.class))).thenReturn(List.of(table, chair));

        mockRecipe(table, List.of(ProductionCapacityFactory.createLink(table, wood, "5.0")));
        mockRecipe(chair, List.of(ProductionCapacityFactory.createLink(chair, wood, "2.0")));

        ProductionPlanResponseDTO result = service.generate();

        assertNotNull(result);
        assertEquals(1, result.items.size());
        assertEquals(new BigDecimal("20"), result.items.getFirst().producibleQuantity);
    }

    @Test
    void generate_shouldHandleRestrictiveStock() {
        RawMaterial iron = RawMaterialFactory.createRawMaterialWithCode("IRON");
        iron.setId(2L);
        iron.setStockQuantity(new BigDecimal("10.0"));

        when(RawMaterial.listAll()).thenReturn(List.of(wood, iron));
        when(Product.listAll(any(Sort.class))).thenReturn(List.of(table));

        mockRecipe(table, List.of(
                ProductionCapacityFactory.createLink(table, wood, "1.0"),
                ProductionCapacityFactory.createLink(table, iron, "5.0")
        ));

        ProductionPlanResponseDTO result = service.generate();

        assertEquals(1, result.items.size());
        assertEquals(new BigDecimal("2"), result.items.getFirst().producibleQuantity);
    }

    @Test
    void generate_shouldReturnEmptyItems_whenStockIsZero() {
        wood.setStockQuantity(BigDecimal.ZERO);
        when(RawMaterial.listAll()).thenReturn(List.of(wood));
        when(Product.listAll(any(Sort.class))).thenReturn(List.of(table));

        mockRecipe(table, List.of(ProductionCapacityFactory.createLink(table, wood, "1.0")));

        ProductionPlanResponseDTO result = service.generate();

        assertTrue(result.items.isEmpty());
    }

    @Test
    void generate_shouldHandleProductsWithoutRecipe() {
        when(RawMaterial.listAll()).thenReturn(List.of(wood));
        when(Product.listAll(any(Sort.class))).thenReturn(List.of(table));

        mockRecipe(table, List.of());

        ProductionPlanResponseDTO result = service.generate();

        assertTrue(result.items.isEmpty());
    }
}
