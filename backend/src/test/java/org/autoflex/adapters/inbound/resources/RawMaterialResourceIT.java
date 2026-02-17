package org.autoflex.adapters.inbound.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.UUID;
import org.autoflex.adapters.inbound.dto.request.RawMaterialRequestDTO;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class RawMaterialResourceIT {

  private static final Long EXISTING_RAW_MATERIAL_ID = 1L;
  private static final Long NOT_FOUND_ID = 999_999L;
  private static final Long LINKED_RAW_MATERIAL_ID = 1L;

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldCreateRawMaterial_whenValidRequest() {
    RawMaterialRequestDTO dto = createValidRequest(uniqueCode());
    Response response =
        given().contentType(ContentType.JSON).body(dto).when().post("/raw-materials");

    Integer createdId = response.then().statusCode(201).extract().path("id");

    response
        .then()
        .header("Location", endsWith("/raw-materials/" + createdId))
        .body("id", is(createdId))
        .body("code", is(dto.code))
        .body("name", is(dto.name))
        .body("stockQuantity", is(dto.stockQuantity.floatValue()));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenCodeIsNull() {
    RawMaterialRequestDTO dto = createValidRequest(uniqueCode());
    dto.code = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/raw-materials")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("code"))
        .body("errors[0].message", is("Raw material code is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenCodeIsEmpty() {
    RawMaterialRequestDTO dto = createValidRequest(uniqueCode());
    dto.code = "";

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/raw-materials")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("code"))
        .body("errors[0].message", is("Raw material code is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenNameIsNull() {
    RawMaterialRequestDTO dto = createValidRequest(uniqueCode());
    dto.name = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/raw-materials")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("name"))
        .body("errors[0].message", is("Raw material name is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenNameIsEmpty() {
    RawMaterialRequestDTO dto = createValidRequest(uniqueCode());
    dto.name = "";

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/raw-materials")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("name"))
        .body("errors[0].message", is("Raw material name is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenStockQuantityIsNull() {
    RawMaterialRequestDTO dto = createValidRequest(uniqueCode());
    dto.stockQuantity = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/raw-materials")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("stockQuantity"))
        .body("errors[0].message", is("Stock quantity is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenStockQuantityIsNegative() {
    RawMaterialRequestDTO dto = createValidRequest(uniqueCode());
    dto.stockQuantity = new BigDecimal("-1.00");

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/raw-materials")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("stockQuantity"))
        .body("errors[0].message", is("Stock quantity cannot be negative"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn409_whenCodeAlreadyExist() {
    RawMaterialRequestDTO dto = createValidRequest("MAD-001");

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/raw-materials")
        .then()
        .statusCode(409)
        .body("error", is("Raw material code already exists"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldUpdateRawMaterial_whenValidRequest() {
    Long id = createRawMaterialAndReturnId(createValidRequest(uniqueCode()));
    RawMaterialRequestDTO dto =
        new RawMaterialRequestDTO(uniqueCode(), "Updated Name", new BigDecimal("222.00"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/raw-materials/{id}", id)
        .then()
        .statusCode(200)
        .body("id", is(id.intValue()))
        .body("code", is(dto.code))
        .body("name", is(dto.name))
        .body("stockQuantity", is(222.00f));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn404_whenIdNotExist() {
    RawMaterialRequestDTO dto = createValidRequest(uniqueCode());

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/raw-materials/{id}", NOT_FOUND_ID)
        .then()
        .statusCode(404)
        .body("error", is("Raw material with id " + NOT_FOUND_ID + " not found"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn409_whenCodeAlreadyExist() {
    Long id = createRawMaterialAndReturnId(createValidRequest(uniqueCode()));
    RawMaterialRequestDTO dto = createValidRequest("MAD-001");

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/raw-materials/{id}", id)
        .then()
        .statusCode(409)
        .body("error", is("Raw material code already exists"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldUpdateRawMaterial_whenCodeBelongsToSameRawMaterial() {
    RawMaterialRequestDTO original = createValidRequest(uniqueCode());
    Long id = createRawMaterialAndReturnId(original);

    RawMaterialRequestDTO dto =
        new RawMaterialRequestDTO(original.code, "Same Code Updated", new BigDecimal("300.00"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/raw-materials/{id}", id)
        .then()
        .statusCode(200)
        .body("id", is(id.intValue()))
        .body("code", is(original.code))
        .body("name", is(dto.name));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn422_whenCodeIsNull() {
    RawMaterialRequestDTO dto = createValidRequest(uniqueCode());
    dto.code = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/raw-materials/{id}", EXISTING_RAW_MATERIAL_ID)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("code"))
        .body("errors[0].message", is("Raw material code is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn422_whenNameIsNull() {
    RawMaterialRequestDTO dto = createValidRequest(uniqueCode());
    dto.name = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/raw-materials/{id}", EXISTING_RAW_MATERIAL_ID)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("name"))
        .body("errors[0].message", is("Raw material name is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn422_whenStockQuantityIsNull() {
    RawMaterialRequestDTO dto = createValidRequest(uniqueCode());
    dto.stockQuantity = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/raw-materials/{id}", EXISTING_RAW_MATERIAL_ID)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("stockQuantity"))
        .body("errors[0].message", is("Stock quantity is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void delete_shouldDeleteRawMaterial_whenValidRequest() {
    Long id = createRawMaterialAndReturnId(createValidRequest(uniqueCode()));

    given().when().delete("/raw-materials/{id}", id).then().statusCode(204);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void delete_shouldReturn404_whenIdNotExist() {
    given()
        .when()
        .delete("/raw-materials/{id}", NOT_FOUND_ID)
        .then()
        .statusCode(404)
        .body("error", is("Raw material with id " + NOT_FOUND_ID + " not found"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void delete_shouldReturn400_whenDeleteFails() {
    given()
        .when()
        .delete("/raw-materials/{id}", LINKED_RAW_MATERIAL_ID)
        .then()
        .statusCode(400)
        .body("error", is("Cannot delete raw material because it is referenced by other records"));
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void delete_shouldReturn403_whenUserIsNotAdmin() {
    given().when().delete("/raw-materials/{id}", EXISTING_RAW_MATERIAL_ID).then().statusCode(403);
  }

  @Test
  void findAll_shouldReturn401_whenUserIsNotAuthenticated() {
    given().when().get("/raw-materials").then().statusCode(401);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void findAll_shouldReturnAllRawMaterials_whenValidRequest() {
    given()
        .queryParam("page", 0)
        .queryParam("size", 10)
        .queryParam("sort", "name")
        .queryParam("dir", "asc")
        .when()
        .get("/raw-materials")
        .then()
        .statusCode(200)
        .body("totalElements", greaterThan(0))
        .body("totalPages", greaterThan(0))
        .body("content", nullValue());
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void findAll_shouldReturnEmptyPage_whenNoRawMaterials() {
    given()
        .queryParam("page", 999)
        .queryParam("size", 10)
        .queryParam("sort", "name")
        .queryParam("dir", "asc")
        .when()
        .get("/raw-materials")
        .then()
        .statusCode(200)
        .body("totalPages", greaterThan(0))
        .body("content", nullValue());
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void findAll_shouldUseDefaultPagination_whenQueryParamsAreNotProvided() {
    given()
        .when()
        .get("/raw-materials")
        .then()
        .statusCode(200)
        .body("page", is(0))
        .body("size", is(10));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void findById_shouldReturnRawMaterial_whenIdExists() {
    given()
        .when()
        .get("/raw-materials/{id}", EXISTING_RAW_MATERIAL_ID)
        .then()
        .statusCode(200)
        .body("id", is(EXISTING_RAW_MATERIAL_ID.intValue()))
        .body("code", is("MAD-001"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void findById_shouldReturn404_whenIdNotExist() {
    given()
        .when()
        .get("/raw-materials/{id}", NOT_FOUND_ID)
        .then()
        .statusCode(404)
        .body("error", is("Raw material with id " + NOT_FOUND_ID + " not found"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void findByName_shouldReturnRawMaterials_whenValidRequest() {
    given()
        .queryParam("name", "Pine")
        .queryParam("page", 0)
        .queryParam("size", 10)
        .queryParam("sort", "name")
        .queryParam("dir", "asc")
        .when()
        .get("/raw-materials/search")
        .then()
        .statusCode(200)
        .body("totalElements", greaterThan(0))
        .body("totalPages", greaterThan(0))
        .body("content", nullValue());
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void findByName_shouldReturnEmptyPage_whenNoMatch() {
    given()
        .queryParam("name", "NOT-FOUND-" + UUID.randomUUID())
        .queryParam("page", 0)
        .queryParam("size", 10)
        .queryParam("sort", "name")
        .queryParam("dir", "asc")
        .when()
        .get("/raw-materials/search")
        .then()
        .statusCode(200)
        .body("totalElements", is(0))
        .body("totalPages", is(1))
        .body("content", nullValue());
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void findByName_shouldUseDefaultPagination_whenQueryParamsAreNotProvided() {
    given()
        .queryParam("name", "Pine")
        .when()
        .get("/raw-materials/search")
        .then()
        .statusCode(200)
        .body("page", is(0))
        .body("size", is(10));
  }

  private Long createRawMaterialAndReturnId(RawMaterialRequestDTO dto) {
    Number id =
        given()
            .contentType(ContentType.JSON)
            .body(dto)
            .when()
            .post("/raw-materials")
            .then()
            .statusCode(201)
            .extract()
            .path("id");

    return id.longValue();
  }

  private static RawMaterialRequestDTO createValidRequest(String code) {
    return new RawMaterialRequestDTO(code, "IT MDF Board", new BigDecimal("1000.00"));
  }

  private static String uniqueCode() {
    return "IT-RAW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }
}
