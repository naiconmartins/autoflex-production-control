package org.autoflex.adapters.inbound.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.autoflex.adapters.inbound.dto.request.ProductRawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.request.ProductRequestDTO;
import org.autoflex.application.commands.ProductCommand;
import org.autoflex.fixtures.ProductFixture;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductRawMaterialResourceIT {

  private static final Long EXISTING_PRODUCT_ID = 1L;
  private static final Long EXISTING_RAW_MATERIAL_LINKED_ID = 2L;
  private static final Long AVAILABLE_RAW_MATERIAL_ID = 1L;
  private static final Long NOT_FOUND_ID = 999_999L;

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void add_shouldCreateAssociation_whenValidRequest() {
    ProductRawMaterialRequestDTO dto =
        new ProductRawMaterialRequestDTO(AVAILABLE_RAW_MATERIAL_ID, new BigDecimal("10.00"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products/{productId}/raw-materials", EXISTING_PRODUCT_ID)
        .then()
        .statusCode(201)
        .body("id", is(AVAILABLE_RAW_MATERIAL_ID.intValue()))
        .body("requiredQuantity", is(10.00f));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void add_shouldReturn422_whenRawMaterialIdIsNull() {
    ProductRawMaterialRequestDTO dto =
        new ProductRawMaterialRequestDTO(null, new BigDecimal("10.00"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products/{productId}/raw-materials", EXISTING_PRODUCT_ID)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("id"))
        .body("errors[0].message", is("Raw material id is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void add_shouldReturn422_whenRequiredQuantityIsNull() {
    ProductRawMaterialRequestDTO dto =
        new ProductRawMaterialRequestDTO(AVAILABLE_RAW_MATERIAL_ID, null);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products/{productId}/raw-materials", EXISTING_PRODUCT_ID)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("requiredQuantity"))
        .body("errors[0].message", is("Required quantity is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void add_shouldReturn422_whenRequiredQuantityIsZero() {
    ProductRawMaterialRequestDTO dto =
        new ProductRawMaterialRequestDTO(AVAILABLE_RAW_MATERIAL_ID, BigDecimal.ZERO);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products/{productId}/raw-materials", EXISTING_PRODUCT_ID)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("requiredQuantity"))
        .body("errors[0].message", is("Required quantity must be greater than zero"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void add_shouldReturn404_whenProductNotExists() {
    ProductRawMaterialRequestDTO dto =
        new ProductRawMaterialRequestDTO(AVAILABLE_RAW_MATERIAL_ID, new BigDecimal("10.00"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products/{productId}/raw-materials", NOT_FOUND_ID)
        .then()
        .statusCode(404)
        .body("error", is("Product with id " + NOT_FOUND_ID + " not found"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void add_shouldReturn404_whenRawMaterialNotExists() {
    ProductRawMaterialRequestDTO dto =
        new ProductRawMaterialRequestDTO(NOT_FOUND_ID, new BigDecimal("10.00"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products/{productId}/raw-materials", EXISTING_PRODUCT_ID)
        .then()
        .statusCode(404)
        .body("error", is("Raw material with id " + NOT_FOUND_ID + " not found"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void add_shouldReturn409_whenAssociationAlreadyExists() {
    ProductRawMaterialRequestDTO dto =
        new ProductRawMaterialRequestDTO(EXISTING_RAW_MATERIAL_LINKED_ID, new BigDecimal("10.00"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products/{productId}/raw-materials", EXISTING_PRODUCT_ID)
        .then()
        .statusCode(409)
        .body("error", is("Raw material already linked to product"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void list_shouldReturnAssociations_whenProductExists() {
    given()
        .when()
        .get("/products/{productId}/raw-materials", EXISTING_PRODUCT_ID)
        .then()
        .statusCode(200)
        .body("size()", greaterThan(0))
        .body("id", hasSize(greaterThan(0)));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void list_shouldReturnEmptyList_whenNoAssociations() {
    Long productId = createProductAndReturnId(createValidProductRequest(uniqueCode()));

    given()
        .when()
        .delete(
            "/products/{productId}/raw-materials/{rawMaterialId}",
            productId,
            AVAILABLE_RAW_MATERIAL_ID)
        .then()
        .statusCode(204);

    given()
        .when()
        .get("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(200)
        .body("size()", is(0));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void list_shouldReturn404_whenProductNotExists() {
    given()
        .when()
        .get("/products/{productId}/raw-materials", NOT_FOUND_ID)
        .then()
        .statusCode(404)
        .body("error", is("Product with id " + NOT_FOUND_ID + " not found"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void updateRequiredQuantity_shouldUpdateAssociation_whenValidRequest() {
    ProductRawMaterialRequestDTO dto =
        new ProductRawMaterialRequestDTO(EXISTING_RAW_MATERIAL_LINKED_ID, new BigDecimal("12.00"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put(
            "/products/{productId}/raw-materials/{rawMaterialId}",
            EXISTING_PRODUCT_ID,
            EXISTING_RAW_MATERIAL_LINKED_ID)
        .then()
        .statusCode(200)
        .body("id", is(EXISTING_RAW_MATERIAL_LINKED_ID.intValue()))
        .body("requiredQuantity", is(12.00f));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void updateRequiredQuantity_shouldReturn422_whenRequiredQuantityIsNull() {
    ProductRawMaterialRequestDTO dto =
        new ProductRawMaterialRequestDTO(EXISTING_RAW_MATERIAL_LINKED_ID, null);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put(
            "/products/{productId}/raw-materials/{rawMaterialId}",
            EXISTING_PRODUCT_ID,
            EXISTING_RAW_MATERIAL_LINKED_ID)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("requiredQuantity"))
        .body("errors[0].message", is("Required quantity is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void updateRequiredQuantity_shouldReturn404_whenAssociationNotExists() {
    ProductRawMaterialRequestDTO dto =
        new ProductRawMaterialRequestDTO(NOT_FOUND_ID, new BigDecimal("10.00"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put(
            "/products/{productId}/raw-materials/{rawMaterialId}",
            EXISTING_PRODUCT_ID,
            NOT_FOUND_ID)
        .then()
        .statusCode(404)
        .body(
            "error",
            is(
                "Association not found between Product "
                    + EXISTING_PRODUCT_ID
                    + " and Raw Material "
                    + NOT_FOUND_ID));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void remove_shouldDeleteAssociation_whenValidRequest() {
    Long productId = createProductAndReturnId(createValidProductRequest(uniqueCode()));

    given()
        .when()
        .delete(
            "/products/{productId}/raw-materials/{rawMaterialId}", productId, AVAILABLE_RAW_MATERIAL_ID)
        .then()
        .statusCode(204);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void remove_shouldReturn404_whenAssociationNotExists() {
    given()
        .when()
        .delete(
            "/products/{productId}/raw-materials/{rawMaterialId}",
            EXISTING_PRODUCT_ID,
            NOT_FOUND_ID)
        .then()
        .statusCode(404)
        .body(
            "error",
            is(
                "Association not found between Product ID "
                    + EXISTING_PRODUCT_ID
                    + " and Raw Material ID "
                    + NOT_FOUND_ID));
  }
  

  @Test
  void list_shouldReturn401_whenUserIsNotAuthenticated() {
    given()
        .when()
        .get("/products/{productId}/raw-materials", EXISTING_PRODUCT_ID)
        .then()
        .statusCode(401);
  }

  private static ProductRequestDTO createValidProductRequest(String code) {
    ProductCommand cmd = ProductFixture.createValidProductCommand();
    return new ProductRequestDTO(
        code,
        cmd.name(),
        cmd.price(),
        List.of(
            new ProductRawMaterialRequestDTO(AVAILABLE_RAW_MATERIAL_ID, new BigDecimal("10.00"))));
  }

  private static String uniqueCode() {
    return "IT-PRM-PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }

  private Long createProductAndReturnId(ProductRequestDTO dto) {
    Response response = given().contentType(ContentType.JSON).body(dto).when().post("/products");
    Number id = response.then().statusCode(201).extract().path("id");
    return id.longValue();
  }
}
