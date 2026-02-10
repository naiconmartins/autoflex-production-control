package org.autoflex.web.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.autoflex.factory.ProductFactory;
import org.autoflex.web.dto.ProductRawMaterialRequestDTO;
import org.autoflex.web.dto.ProductRequestDTO;
import org.autoflex.web.dto.ProductResponseDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
public class ProductResourceIT {

    private ProductResponseDTO insertProduct(ProductRequestDTO request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .extract().as(ProductResponseDTO.class);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldPersistData_whenDataIsValid() {
        ProductRequestDTO request = ProductFactory.createUniqueProductRequestDTOWithRawMaterials();

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .header("Location", notNullValue())
                .body("code", is(request.code));
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void insert_shouldPersistData_whenUserRoleIsUser() {
        ProductRequestDTO request = ProductFactory.createUniqueProductRequestDTOWithRawMaterials();

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("code", is(request.code));
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void findById_shouldReturnProduct_whenIdExists() {
        ProductResponseDTO created = insertProduct(ProductFactory.createUniqueProductRequestDTOWithRawMaterials());

        given()
                .pathParam("id", created.getId())
                .when()
                .get("/products/{id}")
                .then()
                .statusCode(200)
                .body("code", is(created.getCode()));
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void update_shouldModifyProduct_whenDataIsValid() {
        ProductResponseDTO created = insertProduct(ProductFactory.createUniqueProductRequestDTOWithRawMaterials());
        ProductRequestDTO request = ProductFactory.createUniqueProductRequestDTOWithRawMaterials();
        request.name = "Updated Name";

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("id", created.getId())
                .when()
                .put("/products/{id}")
                .then()
                .statusCode(200)
                .body("name", is("Updated Name"))
                .body("code", is(request.code));
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void update_shouldModifyProduct_whenUserRoleIsUser() {
        ProductResponseDTO created = insertProduct(ProductFactory.createUniqueProductRequestDTOWithRawMaterials());
        ProductRequestDTO request = ProductFactory.createUniqueProductRequestDTOWithRawMaterials();
        request.name = "Updated By User";

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("id", created.getId())
                .when()
                .put("/products/{id}")
                .then()
                .statusCode(200)
                .body("name", is("Updated By User"))
                .body("code", is(request.code));
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn409_whenCodeIsDuplicate() {
        ProductResponseDTO created = insertProduct(ProductFactory.createUniqueProductRequestDTOWithRawMaterials());
        ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials(created.getCode());

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/products")
                .then()
                .statusCode(409);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void update_shouldReturn409_whenCodeConflictsWithAnotherProduct() {
        ProductRequestDTO other = ProductFactory.createUniqueProductRequestDTOWithRawMaterials();
        insertProduct(other);
        ProductResponseDTO created = insertProduct(ProductFactory.createUniqueProductRequestDTOWithRawMaterials());

        ProductRequestDTO request = ProductFactory.createUniqueProductRequestDTOWithRawMaterials();
        request.code = other.code;

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("id", created.getId())
                .when()
                .put("/products/{id}")
                .then()
                .statusCode(409);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void delete_shouldReturn204_whenIdExists() {
        ProductResponseDTO created = insertProduct(ProductFactory.createUniqueProductRequestDTOWithRawMaterials());

        given()
                .pathParam("id", created.getId())
                .when()
                .delete("/products/{id}")
                .then()
                .statusCode(204);

        given()
                .pathParam("id", created.getId())
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
                .statusCode(422)
                .body("error", is("Invalid data"))
                .body("errors[0].field", notNullValue());
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
    void update_shouldReturn422_whenDataIsInvalid() {
        ProductResponseDTO created = insertProduct(ProductFactory.createUniqueProductRequestDTOWithRawMaterials());
        ProductRequestDTO invalidRequest = new ProductRequestDTO("ERR-02", "", new BigDecimal("-1.0"), List.of());

        given()
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .pathParam("id", created.getId())
                .when()
                .put("/products/{id}")
                .then()
                .statusCode(422)
                .body("error", is("Invalid data"))
                .body("errors[0].field", notNullValue());
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
                .statusCode(404)
                .body("error", containsString("Raw Material not found"));
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

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void delete_shouldReturn404_whenIdDoesNotExist() {
        given()
                .pathParam("id", 999999L)
                .when()
                .delete("/products/{id}")
                .then()
                .statusCode(404)
                .body("error", containsString("not found"));
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void update_shouldReturn404_whenRawMaterialDoesNotExist() {
        ProductRequestDTO insert = ProductFactory.createUniqueProductRequestDTOWithRawMaterials();
        ProductResponseDTO created = insertProduct(insert);

        ProductRequestDTO update = ProductFactory.createUniqueProductRequestDTOWithRawMaterials();
        update.rawMaterials = List.of(new ProductRawMaterialRequestDTO(999999L, BigDecimal.ONE));

        given()
                .contentType(ContentType.JSON)
                .body(update)
                .pathParam("id", created.getId())
                .when()
                .put("/products/{id}")
                .then()
                .statusCode(404)
                .body("error", containsString("Raw Material not found"));
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void findById_shouldReturn404_whenIdDoesNotExist() {
        given()
                .pathParam("id", 999999L)
                .when()
                .get("/products/{id}")
                .then()
                .statusCode(404)
                .body("error", containsString("not found"));
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void search_shouldReturnEmptyPage_whenNameIsBlank() {
        given()
                .queryParam("name", "  ")
                .queryParam("page", -1)
                .queryParam("size", -1)
                .when()
                .get("/products/search")
                .then()
                .statusCode(200)
                .body("content.size()", is(0))
                .body("page", is(0))
                .body("size", is(10));
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void search_shouldReturnMatches_whenNameIsProvided() {
        given()
                .queryParam("name", "Chair")
                .queryParam("page", 0)
                .queryParam("size", 5)
                .when()
                .get("/products/search")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0));
    }
}
