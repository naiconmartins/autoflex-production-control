package org.autoflex.web.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.autoflex.web.dto.ProductRawMaterialRequestDTO;
import org.autoflex.web.dto.ProductRequestDTO;
import org.autoflex.web.dto.RawMaterialRequestDTO;
import org.autoflex.web.dto.RawMaterialResponseDTO;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RawMaterialResourceIT {

    static Long savedRawMaterialId;
    static final String INITIAL_CODE = "RAW-TEST-001";

    @Test
    @Order(1)
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn201_whenDataIsValid() {
        RawMaterialRequestDTO request = new RawMaterialRequestDTO(INITIAL_CODE, "Initial Material", new BigDecimal("100.0"));

        RawMaterialResponseDTO response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/raw-materials")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("code", is(INITIAL_CODE))
                .extract().as(RawMaterialResponseDTO.class);

        savedRawMaterialId = response.getId();
    }

    @Test
    @Order(2)
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn409_whenCodeAlreadyExists() {
        RawMaterialRequestDTO request = new RawMaterialRequestDTO(INITIAL_CODE, "Another Name", new BigDecimal("50.0"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/raw-materials")
                .then()
                .statusCode(409);
    }

    @Test
    @Order(3)
    @TestSecurity(user = "user", roles = "USER")
    void update_shouldReturn200_whenDataIsValid() {
        RawMaterialRequestDTO updateRequest = new RawMaterialRequestDTO(INITIAL_CODE, "Updated Material", new BigDecimal("150.0"));

        given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .pathParam("id", savedRawMaterialId)
                .when()
                .put("/raw-materials/{id}")
                .then()
                .statusCode(200)
                .body("name", is("Updated Material"))
                .body("stockQuantity", is(150.0f));
    }

    @Test
    @Order(4)
    @TestSecurity(user = "user", roles = "USER")
    void findById_shouldReturn200_whenIdExists() {
        given()
                .pathParam("id", savedRawMaterialId)
                .when()
                .get("/raw-materials/{id}")
                .then()
                .statusCode(200)
                .body("id", is(savedRawMaterialId.intValue()))
                .body("code", is(INITIAL_CODE));
    }

    @Test
    @Order(5)
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn422_whenNameIsBlank() {
        RawMaterialRequestDTO invalidRequest = new RawMaterialRequestDTO("CODE", "", new BigDecimal("-10.0"));

        given()
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/raw-materials")
                .then()
                .statusCode(422);
    }

    @Test
    @Order(6)
    @TestSecurity(user = "user", roles = "USER")
    void delete_shouldReturn403_whenUserIsNotAdmin() {
        given()
                .pathParam("id", savedRawMaterialId)
                .when()
                .delete("/raw-materials/{id}")
                .then()
                .statusCode(403);
    }

    @Test
    @Order(7)
    @TestSecurity(user = "user", roles = "USER")
    void findById_shouldReturn404_whenIdDoesNotExist() {
        given()
                .pathParam("id", 9999)
                .when()
                .get("/raw-materials/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(8)
    @TestSecurity(user = "admin", roles = "ADMIN")
    void delete_shouldReturn204_whenNoConstraintsExist() {
        Number idGen = given()
                .contentType(ContentType.JSON)
                .body(new RawMaterialRequestDTO("DELETE-OK", "Unlinked Material", BigDecimal.ONE))
                .post("/raw-materials")
                .then().statusCode(201).extract().path("id");

        Long rmId = idGen.longValue();

        given()
                .pathParam("id", rmId)
                .when()
                .delete("/raw-materials/{id}")
                .then()
                .statusCode(204);

        given()
                .pathParam("id", rmId)
                .when()
                .get("/raw-materials/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(9)
    @TestSecurity(user = "admin", roles = "ADMIN")
    void delete_shouldReturn400_whenConstraintViolationOccurs() {
        Number idGen = given()
                .contentType(ContentType.JSON)
                .body(new RawMaterialRequestDTO("FK-CONSTRAINT", "Linked Material", BigDecimal.TEN))
                .post("/raw-materials")
                .then().statusCode(201).extract().path("id");

        Long rmId = idGen.longValue();

        ProductRequestDTO productRequest = new ProductRequestDTO(
                "PROD-LOCK", "Blocking Product", new BigDecimal("100"),
                List.of(new ProductRawMaterialRequestDTO(rmId, BigDecimal.ONE)));

        given()
                .contentType(ContentType.JSON)
                .body(productRequest)
                .post("/products")
                .then().statusCode(201);

        given()
                .pathParam("id", rmId)
                .when()
                .delete("/raw-materials/{id}")
                .then()
                .statusCode(400)
                .body("error", containsString("Cannot delete raw material because it is referenced by other records"));
    }

    @Test
    @Order(10)
    @TestSecurity(user = "admin", roles = "ADMIN")
    void update_shouldReturn409_whenCodeConflicts() {
        given().contentType(ContentType.JSON)
                .body(new RawMaterialRequestDTO("CODE-A", "Material A", BigDecimal.ONE))
                .post("/raw-materials");

        Integer idBInt = given().contentType(ContentType.JSON)
                .body(new RawMaterialRequestDTO("CODE-B", "Material B", BigDecimal.ONE))
                .post("/raw-materials")
                .then()
                .extract().path("id");

        Long idB = idBInt.longValue();

        RawMaterialRequestDTO conflictRequest = new RawMaterialRequestDTO("CODE-A", "New Name", BigDecimal.ONE);

        given()
                .contentType(ContentType.JSON)
                .body(conflictRequest)
                .pathParam("id", idB)
                .when()
                .put("/raw-materials/{id}")
                .then()
                .statusCode(409);
    }

    @Test
    @Order(11)
    @TestSecurity(user = "admin", roles = "ADMIN")
    void update_shouldReturn404_whenIdDoesNotExist() {
        RawMaterialRequestDTO request = new RawMaterialRequestDTO("NEW", "Name", BigDecimal.TEN);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("id", 99999)
                .when()
                .put("/raw-materials/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(12)
    @TestSecurity(user = "user", roles = "USER")
    void findAll_shouldReturnPagedData() {
        given()
                .queryParam("page", 0)
                .queryParam("size", 1)
                .queryParam("sort", "name")
                .queryParam("dir", "asc")
                .when()
                .get("/raw-materials")
                .then()
                .statusCode(200)
                .body("content", notNullValue())
                .body("content.size()", is(1))
                .body("size", is(1));
    }
}
