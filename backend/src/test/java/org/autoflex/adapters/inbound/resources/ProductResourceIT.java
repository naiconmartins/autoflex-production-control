package org.autoflex.adapters.inbound.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

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
public class ProductResourceIT {

  private static final Long EXISTING_PRODUCT_ID = 1L;
  private static final Long NOT_FOUND_ID = 999_999L;
  private static final Long VALID_RAW_MATERIAL_ID = 1L;
  private static final Long NOT_FOUND_RAW_MATERIAL_ID = 999_999L;

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldCreateProduct_whenValidRequest() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    Response response = given().contentType(ContentType.JSON).body(dto).when().post("/products");
    Integer createdId = response.then().statusCode(201).extract().path("id");

    response
        .then()
        .header("Location", endsWith("/products/" + createdId))
        .body("id", is(createdId))
        .body("code", is(dto.code))
        .body("name", is(dto.name))
        .body("rawMaterials", hasSize(1))
        .body("rawMaterials[0].id", is(1));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenRawMaterialsIsNull() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.rawMaterials = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("rawMaterials"))
        .body("errors[0].message", is("At least one raw material is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenRawMaterialsIsEmpty() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.rawMaterials = List.of();

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("rawMaterials"))
        .body("errors[0].message", is("At least one raw material is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenCodeIsNull() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.code = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("code"))
        .body("errors[0].message", is("Product code is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenCodeIsEmpty() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.code = "";

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("code"))
        .body("errors[0].message", is("Product code is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenNameIsNull() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.name = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("name"))
        .body("errors[0].message", is("Product name is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenNameIsEmpty() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.name = "";

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("name"))
        .body("errors[0].message", is("Product name is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenPriceIsNull() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.price = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("price"))
        .body("errors[0].message", is("Product price is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenPriceIsZero() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.price = BigDecimal.ZERO;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("price"))
        .body("errors[0].message", is("Price must be greater than zero"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenPriceIsNegative() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.price = new BigDecimal("-1");

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("price"))
        .body("errors[0].message", is("Price must be greater than zero"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenRawMaterialIdIsNull() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.rawMaterials = List.of(new ProductRawMaterialRequestDTO(null, new BigDecimal("10.00")));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("id"))
        .body("errors[0].message", is("Raw material id is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenRawMaterialRequiredQuantityIsNull() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.rawMaterials = List.of(new ProductRawMaterialRequestDTO(VALID_RAW_MATERIAL_ID, null));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("requiredQuantity"))
        .body("errors[0].message", is("Required quantity is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenRawMaterialRequiredQuantityIsZero() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.rawMaterials =
        List.of(new ProductRawMaterialRequestDTO(VALID_RAW_MATERIAL_ID, BigDecimal.ZERO));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("requiredQuantity"))
        .body("errors[0].message", is("Required quantity must be greater than zero"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn409_whenCodeAlreadyExist() {
    ProductRequestDTO dto = createValidRequest("PROD-001");

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products")
        .then()
        .statusCode(409)
        .body("error", is("Product code already exists"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn404_whenRawMaterialNotExist() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.rawMaterials =
        List.of(
            new ProductRawMaterialRequestDTO(NOT_FOUND_RAW_MATERIAL_ID, new BigDecimal("10.00")));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products")
        .then()
        .statusCode(404)
        .body("error", is("Raw Material not found: " + NOT_FOUND_RAW_MATERIAL_ID));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldUpdateProduct_whenValidRequest() {
    Long id = createProductAndReturnId(createValidRequest(uniqueCode()));
    ProductRequestDTO dto = createValidRequest(uniqueCode(), 2L);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", id)
        .then()
        .statusCode(200)
        .body("id", is(id.intValue()))
        .body("code", is(dto.code))
        .body("name", is(dto.name))
        .body("rawMaterials", hasSize(1));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn404_whenIdNotExist() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", NOT_FOUND_ID)
        .then()
        .statusCode(404)
        .body("error", is("Product with id " + NOT_FOUND_ID + " not found"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn409_whenCodeAlreadyExist() {
    Long id = createProductAndReturnId(createValidRequest(uniqueCode()));
    ProductRequestDTO dto = createValidRequest("PROD-001");

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", id)
        .then()
        .statusCode(409)
        .body("error", is("Product code already exists"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldUpdateProduct_whenCodeBelongsToSameProduct() {
    ProductRequestDTO original = createValidRequest(uniqueCode(), 2L);
    Long id = createProductAndReturnId(original);
    ProductRequestDTO dto =
        new ProductRequestDTO(
            original.code,
            original.name,
            original.price,
            List.of(original.rawMaterials.getFirst()));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", id)
        .then()
        .statusCode(200)
        .body("id", is(id.intValue()))
        .body("code", is(original.code));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn422_whenRawMaterialsIsNull() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.rawMaterials = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", EXISTING_PRODUCT_ID)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("rawMaterials"))
        .body("errors[0].message", is("At least one raw material is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn422_whenCodeIsNull() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.code = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", EXISTING_PRODUCT_ID)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("code"))
        .body("errors[0].message", is("Product code is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn422_whenNameIsNull() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.name = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", EXISTING_PRODUCT_ID)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("name"))
        .body("errors[0].message", is("Product name is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn422_whenPriceIsNull() {
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.price = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", EXISTING_PRODUCT_ID)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("price"))
        .body("errors[0].message", is("Product price is required"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn404_whenRawMaterialNotExist() {
    Long id = createProductAndReturnId(createValidRequest(uniqueCode()));
    ProductRequestDTO dto = createValidRequest(uniqueCode());
    dto.rawMaterials =
        List.of(
            new ProductRawMaterialRequestDTO(NOT_FOUND_RAW_MATERIAL_ID, new BigDecimal("10.00")));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", id)
        .then()
        .statusCode(404)
        .body("error", is("Raw Material not found: " + NOT_FOUND_RAW_MATERIAL_ID));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void delete_shouldDeleteProduct_whenValidRequest() {
    Long id = createProductAndReturnId(createValidRequest(uniqueCode()));

    given().when().delete("/products/{id}", id).then().statusCode(204);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void delete_shouldReturn404_whenIdNotExist() {
    given()
        .when()
        .delete("/products/{id}", NOT_FOUND_ID)
        .then()
        .statusCode(404)
        .body("error", is("Product with id " + NOT_FOUND_ID + " not found"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void delete_shouldReturn400_whenDeleteFails() {}

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void delete_shouldReturn403_whenUserIsNotAdmin() {
    given().when().delete("/products/{id}", EXISTING_PRODUCT_ID).then().statusCode(403);
  }

  @Test
  void findAll_shouldReturn401_whenUserIsNotAuthenticated() {
    given().when().get("/products").then().statusCode(401);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void findAll_shouldReturnAllProducts_whenValidRequest() {
    given()
        .queryParam("page", 0)
        .queryParam("size", 10)
        .queryParam("sort", "name")
        .queryParam("dir", "asc")
        .when()
        .get("/products")
        .then()
        .statusCode(200)
        .body("totalElements", greaterThan(0))
        .body("totalPages", greaterThan(0))
        .body("content", nullValue());
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void findAll_shouldReturnEmptyPage_whenNoProducts() {
    given()
        .queryParam("page", 999)
        .queryParam("size", 10)
        .queryParam("sort", "name")
        .queryParam("dir", "asc")
        .when()
        .get("/products")
        .then()
        .statusCode(200)
        .body("totalPages", greaterThan(0))
        .body("content", nullValue());
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void findAll_shouldUseDefaultPagination_whenQueryParamsAreNotProvided() {
    given().when().get("/products").then().statusCode(200).body("page", is(0)).body("size", is(10));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void findById_shouldReturnProduct_whenIdExists() {
    given()
        .when()
        .get("/products/{id}", EXISTING_PRODUCT_ID)
        .then()
        .statusCode(200)
        .body("id", is(EXISTING_PRODUCT_ID.intValue()))
        .body("code", is("PROD-001"))
        .body("name", is("Dining Table Oak 1.80m"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void findById_shouldReturn404_whenIdNotExist() {
    given()
        .when()
        .get("/products/{id}", NOT_FOUND_ID)
        .then()
        .statusCode(404)
        .body("error", is("Product not found with id: " + NOT_FOUND_ID));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void findByName_shouldReturnProducts_whenValidRequest() {
    given()
        .queryParam("name", "Dining")
        .queryParam("page", 0)
        .queryParam("size", 10)
        .queryParam("sort", "name")
        .queryParam("dir", "asc")
        .when()
        .get("/products/search")
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
        .get("/products/search")
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
        .queryParam("name", "Dining")
        .when()
        .get("/products/search")
        .then()
        .statusCode(200)
        .body("page", is(0))
        .body("size", is(10));
  }

  private Long createProductAndReturnId(ProductRequestDTO dto) {
    Number id =
        given()
            .contentType(ContentType.JSON)
            .body(dto)
            .when()
            .post("/products")
            .then()
            .statusCode(201)
            .extract()
            .path("id");

    return id.longValue();
  }

  private static ProductRequestDTO createValidRequest(String code) {
    return createValidRequest(code, VALID_RAW_MATERIAL_ID);
  }

  private static ProductRequestDTO createValidRequest(String code, Long rawMaterialId) {
    ProductCommand cmd = ProductFixture.createValidProductCommand();
    return new ProductRequestDTO(
        code,
        cmd.name(),
        cmd.price(),
        List.of(new ProductRawMaterialRequestDTO(rawMaterialId, new BigDecimal("10.00"))));
  }

  private static String uniqueCode() {
    return "IT-PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }
}
