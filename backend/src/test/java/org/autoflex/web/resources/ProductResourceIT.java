package org.autoflex.web.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.autoflex.factory.ProductFactory;
import org.autoflex.factory.ProductRawMaterialFactory;
import org.autoflex.web.dto.ProductRawMaterialRequestDTO;
import org.autoflex.web.dto.ProductRequestDTO;
import org.autoflex.web.dto.ProductResponseDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductResourceIT {

    static Long databaseProductId;
    static final String EXISTING_CODE = "PROD-001";
    static final String ANOTHER_EXISTING_CODE = "PROD-005";

    @Test
    @Order(1)
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldPersistData_whenDataIsValid() {
        ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

        ProductResponseDTO response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract().as(ProductResponseDTO.class);

        databaseProductId = response.getId();
    }

    @Test
    @Order(2)
    @TestSecurity(user = "user", roles = "USER")
    void findById_shouldReturnProduct_whenIdExists() {
        given()
                .pathParam("id", 5)
                .when()
                .get("/products/{id}")
                .then()
                .statusCode(200)
                .body("code", is(ANOTHER_EXISTING_CODE));
    }

    @Test
    @Order(3)
    @TestSecurity(user = "admin", roles = "ADMIN")
    void update_shouldModifyProduct_whenDataIsValid() {
        ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();
        request.name = "Updated Name";
        request.code = EXISTING_CODE;

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("id", 1)
                .when()
                .put("/products/{id}")
                .then()
                .statusCode(200)
                .body("name", is("Updated Name"))
                .body("code", is(EXISTING_CODE));
    }

    @Test
    @Order(4)
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn409_whenCodeIsDuplicate() {
        ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();
        request.code = EXISTING_CODE;

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/products")
                .then()
                .statusCode(409);
    }

    @Test
    @Order(5)
    @TestSecurity(user = "admin", roles = "ADMIN")
    void update_shouldReturn409_whenCodeConflictsWithAnotherProduct() {
        ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();
        request.code = ANOTHER_EXISTING_CODE;

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("id", 1)
                .when()
                .put("/products/{id}")
                .then()
                .statusCode(409);
    }

    @Test
    @Order(6)
    @TestSecurity(user = "admin", roles = "ADMIN")
    void delete_shouldReturn204_whenIdExists() {
        given()
                .pathParam("id", databaseProductId)
                .when()
                .delete("/products/{id}")
                .then()
                .statusCode(204);

        given()
                .pathParam("id", databaseProductId)
                .when()
                .get("/products/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn422_whenDataIsInvalid() {
        ProductRequestDTO invalidRequest = new ProductRequestDTO("ERR-01", "", new BigDecimal("-1.0"), List.of());

        given()
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/products")
                .then()
                .statusCode(422);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void update_shouldReturn404_whenIdDoesNotExist() {
        ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("id", 9999L)
                .when()
                .put("/products/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn404_whenRawMaterialNotFound() {
        ProductRawMaterialRequestDTO nonexistentMaterial = new ProductRawMaterialRequestDTO(999L, new BigDecimal("10.0"));
        ProductRequestDTO request = new ProductRequestDTO("PROD-NEW-UNIT", "New", new BigDecimal("10.0"), List.of(nonexistentMaterial));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/products")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void delete_shouldReturn403_whenUserIsNotAdmin() {
        given()
                .pathParam("id", 1L)
                .when()
                .delete("/products/{id}")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void findAll_shouldReturnPagedData() {
        given()
                .queryParam("page", 0)
                .queryParam("size", 5)
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("content", notNullValue());
    }
}
