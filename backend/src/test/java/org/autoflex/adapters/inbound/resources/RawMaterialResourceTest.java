package org.autoflex.adapters.inbound.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.List;
import org.autoflex.adapters.inbound.dto.request.RawMaterialRequestDTO;
import org.autoflex.application.commands.RawMaterialCommand;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.application.dto.SearchQuery;
import org.autoflex.application.usecases.RawMaterialUseCase;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.DatabaseException;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.domain.RawMaterial;
import org.autoflex.fixtures.RawMaterialFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestSecurity(
    user = "test-user",
    roles = {"ADMIN", "USER"})
public class RawMaterialResourceTest {

  @InjectMock RawMaterialUseCase rawMaterialUseCase;

  private RawMaterialRequestDTO dto;
  private RawMaterial rawMaterial;
  private Long existingId;

  @BeforeEach
  void setUp() {
    dto = createValidRequest();
    existingId = 1L;
    rawMaterial = createRawMaterialForResponse(existingId);
  }

  @Test
  void insert_shouldCreateRawMaterial_whenValidRequest() {
    when(rawMaterialUseCase.insert(any(RawMaterialCommand.class))).thenReturn(rawMaterial);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/raw-materials")
        .then()
        .statusCode(201)
        .header("Location", endsWith("/raw-materials/1"))
        .body("id", is(1))
        .body("code", is(dto.code))
        .body("name", is(dto.name))
        .body("stockQuantity", is(1000.00f));
  }

  @Test
  void insert_shouldReturn422_whenCodeIsNull() {
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
  void insert_shouldReturn422_whenCodeIsEmpty() {
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
  void insert_shouldReturn422_whenNameIsNull() {
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
  void insert_shouldReturn422_whenNameIsEmpty() {
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
  void insert_shouldReturn422_whenStockQuantityIsNull() {
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
  void insert_shouldReturn422_whenStockQuantityIsNegative() {
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
  void insert_shouldReturn409_whenCodeAlreadyExist() {
    when(rawMaterialUseCase.insert(any(RawMaterialCommand.class)))
        .thenThrow(new ConflictException("Raw material code already exists"));

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
  void update_shouldUpdateRawMaterial_whenValidRequest() {
    when(rawMaterialUseCase.update(eq(existingId), any(RawMaterialCommand.class)))
        .thenReturn(rawMaterial);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/raw-materials/{id}", existingId)
        .then()
        .statusCode(200)
        .body("id", is(1))
        .body("code", is(dto.code))
        .body("name", is(dto.name))
        .body("stockQuantity", is(1000.00f));
  }

  @Test
  void update_shouldReturn404_whenIdNotExist() {
    when(rawMaterialUseCase.update(eq(2L), any(RawMaterialCommand.class)))
        .thenThrow(new ResourceNotFoundException("Raw material with id 2 not found"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/raw-materials/{id}", 2L)
        .then()
        .statusCode(404)
        .body("error", is("Raw material with id 2 not found"));
  }

  @Test
  void update_shouldReturn409_whenCodeAlreadyExist() {
    when(rawMaterialUseCase.update(eq(existingId), any(RawMaterialCommand.class)))
        .thenThrow(new ConflictException("Raw material code already exists"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/raw-materials/{id}", existingId)
        .then()
        .statusCode(409)
        .body("error", is("Raw material code already exists"));
  }

  @Test
  void update_shouldUpdateRawMaterial_whenCodeBelongsToSameRawMaterial() {
    when(rawMaterialUseCase.update(eq(existingId), any(RawMaterialCommand.class)))
        .thenReturn(rawMaterial);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/raw-materials/{id}", existingId)
        .then()
        .statusCode(200)
        .body("id", is(1))
        .body("code", is(dto.code));
  }

  @Test
  void update_shouldReturn422_whenCodeIsNull() {
    dto.code = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/raw-materials/{id}", existingId)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("code"))
        .body("errors[0].message", is("Raw material code is required"));
  }

  @Test
  void update_shouldReturn422_whenNameIsNull() {
    dto.name = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/raw-materials/{id}", existingId)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("name"))
        .body("errors[0].message", is("Raw material name is required"));
  }

  @Test
  void update_shouldReturn422_whenStockQuantityIsNull() {
    dto.stockQuantity = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/raw-materials/{id}", existingId)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("stockQuantity"))
        .body("errors[0].message", is("Stock quantity is required"));
  }

  @Test
  void delete_shouldDeleteRawMaterial_whenValidRequest() {
    given().when().delete("/raw-materials/{id}", existingId).then().statusCode(204);
  }

  @Test
  void delete_shouldReturn404_whenIdNotExist() {
    doThrow(new ResourceNotFoundException("Raw material with id 1 not found"))
        .when(rawMaterialUseCase)
        .delete(existingId);

    given()
        .when()
        .delete("/raw-materials/{id}", existingId)
        .then()
        .statusCode(404)
        .body("error", is("Raw material with id 1 not found"));
  }

  @Test
  void delete_shouldReturn400_whenDeleteFails() {
    doThrow(
            new DatabaseException(
                "Cannot delete raw material because it is referenced by other records"))
        .when(rawMaterialUseCase)
        .delete(existingId);

    given()
        .when()
        .delete("/raw-materials/{id}", existingId)
        .then()
        .statusCode(400)
        .body("error", is("Cannot delete raw material because it is referenced by other records"));
  }

  @Test
  @TestSecurity(
      user = "test-user",
      roles = {"USER"})
  void delete_shouldReturn403_whenUserIsNotAdmin() {
    given().when().delete("/raw-materials/{id}", existingId).then().statusCode(403);
  }

  @Test
  @TestSecurity(user = "")
  void findAll_shouldReturn401_whenUserIsNotAuthenticated() {
    given().when().get("/raw-materials").then().statusCode(401);
  }

  @Test
  void findAll_shouldReturnAllRawMaterials_whenValidRequest() {
    PagedModel<RawMaterial> paged = new PagedModel<>(List.of(rawMaterial), 1L, 1);
    when(rawMaterialUseCase.findAll(any())).thenReturn(paged);

    given()
        .queryParam("page", 0)
        .queryParam("size", 10)
        .queryParam("sort", "name")
        .queryParam("dir", "asc")
        .when()
        .get("/raw-materials")
        .then()
        .statusCode(200)
        .body("totalElements", is(1))
        .body("totalPages", is(1))
        .body("content", nullValue());
  }

  @Test
  void findAll_shouldReturnEmptyPage_whenNoRawMaterials() {
    when(rawMaterialUseCase.findAll(any())).thenReturn(new PagedModel<>(List.of(), 0L, 0));

    given()
        .queryParam("page", 0)
        .queryParam("size", 10)
        .queryParam("sort", "name")
        .queryParam("dir", "asc")
        .when()
        .get("/raw-materials")
        .then()
        .statusCode(200)
        .body("totalElements", is(0))
        .body("totalPages", is(0))
        .body("content", nullValue());
  }

  @Test
  void findAll_shouldUseDefaultPagination_whenQueryParamsAreNotProvided() {
    when(rawMaterialUseCase.findAll(any())).thenReturn(new PagedModel<>(List.of(), 0L, 0));

    given().when().get("/raw-materials").then().statusCode(200);

    verify(rawMaterialUseCase).findAll(eq(new SearchQuery(0, 10, "name", "asc")));
  }

  @Test
  void findById_shouldReturnRawMaterial_whenIdExists() {
    when(rawMaterialUseCase.findById(existingId)).thenReturn(rawMaterial);

    given()
        .when()
        .get("/raw-materials/{id}", existingId)
        .then()
        .statusCode(200)
        .body("id", is(1))
        .body("code", is(dto.code))
        .body("name", is(dto.name))
        .body("stockQuantity", is(1000.00f));
  }

  @Test
  void findById_shouldReturn404_whenIdNotExist() {
    when(rawMaterialUseCase.findById(existingId))
        .thenThrow(new ResourceNotFoundException("Raw material with id 1 not found"));

    given()
        .when()
        .get("/raw-materials/{id}", existingId)
        .then()
        .statusCode(404)
        .body("error", is("Raw material with id 1 not found"));
  }

  @Test
  void findByName_shouldReturnRawMaterials_whenValidRequest() {
    PagedModel<RawMaterial> paged = new PagedModel<>(List.of(rawMaterial), 1L, 1);
    when(rawMaterialUseCase.findByName(eq("MDF"), any())).thenReturn(paged);

    given()
        .queryParam("name", "MDF")
        .queryParam("page", 0)
        .queryParam("size", 10)
        .queryParam("sort", "name")
        .queryParam("dir", "asc")
        .when()
        .get("/raw-materials/search")
        .then()
        .statusCode(200)
        .body("totalElements", is(1))
        .body("totalPages", is(1))
        .body("content", nullValue());
  }

  @Test
  void findByName_shouldReturnEmptyPage_whenNoMatch() {
    when(rawMaterialUseCase.findByName(eq("NotFound"), any()))
        .thenReturn(new PagedModel<>(List.of(), 0L, 0));

    given()
        .queryParam("name", "NotFound")
        .queryParam("page", 0)
        .queryParam("size", 10)
        .queryParam("sort", "name")
        .queryParam("dir", "asc")
        .when()
        .get("/raw-materials/search")
        .then()
        .statusCode(200)
        .body("totalElements", is(0))
        .body("totalPages", is(0))
        .body("content", nullValue());
  }

  @Test
  void findByName_shouldUseDefaultPagination_whenQueryParamsAreNotProvided() {
    when(rawMaterialUseCase.findByName(eq("MDF"), any()))
        .thenReturn(new PagedModel<>(List.of(), 0L, 0));

    given().queryParam("name", "MDF").when().get("/raw-materials/search").then().statusCode(200);

    verify(rawMaterialUseCase).findByName(eq("MDF"), eq(new SearchQuery(0, 10, "name", "asc")));
  }

  private static RawMaterialRequestDTO createValidRequest() {
    RawMaterialCommand cmd = RawMaterialFixture.createValidRawMaterialCommand();
    return new RawMaterialRequestDTO(cmd.code(), cmd.name(), cmd.stockQuantity());
  }

  private static RawMaterial createRawMaterialForResponse(Long id) {
    return RawMaterialFixture.createRawMaterial(id);
  }
}
