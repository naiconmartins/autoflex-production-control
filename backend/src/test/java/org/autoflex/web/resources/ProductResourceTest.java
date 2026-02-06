package org.autoflex.web.resources;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.autoflex.application.services.ProductService;
import org.autoflex.factory.ProductFactory;
import org.autoflex.factory.ProductRawMaterialFactory;
import org.autoflex.web.dto.*;
import org.autoflex.web.exceptions.ConflictException;
import org.autoflex.web.exceptions.DatabaseException;
import org.autoflex.web.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@QuarkusTest
public class ProductResourceTest {

    @InjectMock
    ProductService productService;

    @Test
    void insert_shouldReturn401_whenUserIsNotAuthenticated() {
        ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/product")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn201_whenDataIsValid() {
        ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();
        ProductResponseDTO response = ProductFactory.createProductResponseDTOWithRawMaterials();

        when(productService.insert(any(ProductRequestDTO.class))).thenReturn(response);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/product")
                .then()
                .statusCode(201)
                .header("Location", org.hamcrest.Matchers.containsString("/product/1"))
                .body("code", is("PROD001"));
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn422_whenRawMaterialsIsEmpty() {
        ProductRequestDTO request = ProductFactory.createProductRequestDTO();

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/product")
                .then()
                .statusCode(422);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn422_whenFieldsIsIsEmpty() {
        List<ProductRawMaterialRequestDTO> productRawMaterial = ProductRawMaterialFactory.createListOfRawMaterialsRequestDTO();

        ProductRequestDTO request = new ProductRequestDTO("", "", new BigDecimal("10.0"), productRawMaterial);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/product")
                .then()
                .statusCode(422);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn409_whenCodeAlreadyExists() {
        ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

        when(productService.insert(any(ProductRequestDTO.class)))
                .thenThrow(new ConflictException("Product code already exists"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/product")
                .then()
                .statusCode(409);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn404_whenRawMaterialNotFound() {
        ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

        when(productService.insert(any(ProductRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Raw material not found"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/product")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void update_shouldReturn200_whenDataIsValid() {
        Long id = 1L;
        ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();
        ProductResponseDTO response = ProductFactory.createProductResponseDTOWithRawMaterials();

        when(productService.update(eq(id), any(ProductRequestDTO.class))).thenReturn(response);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/product/{id}", id)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is(response.name));
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void update_shouldReturn409_whenCodeAlreadyExistsInAnotherProduct() {
        Long id = 1L;
        ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

        when(productService.update(eq(id), any(ProductRequestDTO.class)))
                .thenThrow(new ConflictException("Product code already exists"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/product/{id}", id)
                .then()
                .statusCode(409);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void update_shouldReturn404_whenRawMaterialNotFound() {
        Long id = 1L;
        ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

        when(productService.update(eq(id), any(ProductRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Raw material not found"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/product/{id}", id)
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void update_shouldReturn404_whenIdDoesNotExist() {
        Long invalidId = 99L;
        ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

        when(productService.update(eq(invalidId), any(ProductRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Product not found"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/product/{id}", invalidId)
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void delete_shouldReturn204_whenIdExists() {
        Long id = 1L;

        given()
                .when()
                .delete("/product/{id}", id)
                .then()
                .statusCode(204);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void delete_shouldReturn403_whenUserIsNotAdmin() {
        given()
                .when()
                .delete("/product/1")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void delete_shouldReturn400_whenDatabaseExceptionOccurs() {
        Long id = 1L;

        doThrow(new DatabaseException("Cannot delete product..."))
                .when(productService).delete(id);

        given()
                .when()
                .delete("/product/{id}", id)
                .then()
                .statusCode(400);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void findById_shouldReturn200_whenIdExists() {
        Long id = 1L;
        ProductResponseDTO response = ProductFactory.createProductResponseDTOWithRawMaterials();

        when(productService.findById(id)).thenReturn(response);

        given()
                .when()
                .get("/product/{id}", id)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("code", is("PROD001"));
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void findById_shouldReturn404_whenIdDoesNotExist() {
        Long invalidId = 99L;

        when(productService.findById(invalidId))
                .thenThrow(new ResourceNotFoundException("Product with id " + invalidId + " not found"));

        given()
                .when()
                .get("/product/{id}", invalidId)
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void findAll_shouldReturnPageResponse_whenCalled() {
        ProductResponseDTO p1 = ProductFactory.createProductResponseDTOWithRawMaterials();

        PageResponseDTO<ProductResponseDTO> pageResponse = new PageResponseDTO<>(
                List.of(p1), 1L, 1, 0, 10
        );

        when(productService.findAll(any(PageRequestDTO.class))).thenReturn(pageResponse);

        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("sortBy", "name")
                .queryParam("direction", "asc")
                .when()
                .get("/product")
                .then()
                .statusCode(200)
                .body("totalElements", is(1))
                .body("content[0].code", is("PROD001"));
    }
}
