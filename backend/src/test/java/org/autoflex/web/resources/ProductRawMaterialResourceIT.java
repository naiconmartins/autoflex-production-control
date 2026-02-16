package org.autoflex.web.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasItem;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import org.autoflex.adapters.inbound.dto.request.ProductRawMaterialRequestDTO;
import org.autoflex.domain.Product;
import org.autoflex.domain.RawMaterial;
import org.autoflex.factory.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductRawMaterialResourceIT {

  Long productId;
  Long rawMaterialId;
  Long otherRawMaterialId;

  @BeforeEach
  @Transactional
  void setUp() {
    String productCode = TestData.unique("PRM-PROD");
    String rmCode = TestData.unique("PRM-RM");
    String otherRmCode = TestData.unique("PRM-RM-OTHER");

    Product.delete("code", productCode);
    RawMaterial.delete("code", rmCode);
    RawMaterial.delete("code", otherRmCode);

    Product product = new Product(productCode, "PRM Product", new BigDecimal("10.00"));
    product.persist();
    Product.flush();

    RawMaterial rawMaterial = new RawMaterial(rmCode, "PRM Raw Material", new BigDecimal("100.00"));
    rawMaterial.persist();
    RawMaterial.flush();

    RawMaterial otherRawMaterial =
        new RawMaterial(otherRmCode, "PRM Other Raw Material", new BigDecimal("100.00"));
    otherRawMaterial.persist();
    RawMaterial.flush();

    productId = product.getId();
    rawMaterialId = rawMaterial.getId();
    otherRawMaterialId = otherRawMaterial.getId();
  }

  @Test
  void add_shouldReturn401_whenAnonymous() {
    given()
        .contentType(ContentType.JSON)
        .body(new ProductRawMaterialRequestDTO(rawMaterialId, BigDecimal.ONE))
        .when()
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(401);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void add_shouldReturn201_whenValidRequest() {
    ProductRawMaterialRequestDTO request =
        new ProductRawMaterialRequestDTO(rawMaterialId, new BigDecimal("1.00"));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(201)
        .body("id", is(rawMaterialId.intValue()))
        .body("requiredQuantity", notNullValue())
        .body("requiredQuantity", is(1.0f));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void add_shouldReturn201_whenAdminRole() {
    ProductRawMaterialRequestDTO request =
        new ProductRawMaterialRequestDTO(rawMaterialId, new BigDecimal("1.00"));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(201)
        .body("id", is(rawMaterialId.intValue()));
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void add_shouldReturn409_whenLinkAlreadyExists() {
    ProductRawMaterialRequestDTO request =
        new ProductRawMaterialRequestDTO(rawMaterialId, new BigDecimal("1.00"));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(201);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(409);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void list_shouldReturn200AndEmpty_whenNoLinks() {
    given()
        .when()
        .get("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(200)
        .body("size()", is(0));
  }

  @Test
  void list_shouldReturn401_whenAnonymous() {
    given().when().get("/products/{productId}/raw-materials", productId).then().statusCode(401);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void list_shouldIncludeLinkedRawMaterial() {
    ProductRawMaterialRequestDTO request =
        new ProductRawMaterialRequestDTO(rawMaterialId, new BigDecimal("1.00"));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(201);

    given()
        .when()
        .get("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(200)
        .body("id", hasItem(rawMaterialId.intValue()));
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void add_shouldReturn404_whenProductDoesNotExist() {
    given()
        .contentType(ContentType.JSON)
        .body(new ProductRawMaterialRequestDTO(rawMaterialId, BigDecimal.ONE))
        .when()
        .post("/products/{productId}/raw-materials", 999999L)
        .then()
        .statusCode(404)
        .body("error", containsString("Product with id"));
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void add_shouldReturn404_whenRawMaterialDoesNotExist() {
    given()
        .contentType(ContentType.JSON)
        .body(new ProductRawMaterialRequestDTO(999999L, BigDecimal.ONE))
        .when()
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(404)
        .body("error", containsString("Raw material with id"));
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void add_shouldReturn422_whenRequiredQuantityIsInvalid() {
    given()
        .contentType(ContentType.JSON)
        .body(new ProductRawMaterialRequestDTO(rawMaterialId, BigDecimal.ZERO))
        .when()
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(422)
        .body("error", is("Invalid data"))
        .body("errors[0].field", is("requiredQuantity"));
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void list_shouldReturn404_whenProductDoesNotExist() {
    given()
        .when()
        .get("/products/{productId}/raw-materials", 999999L)
        .then()
        .statusCode(404)
        .body("error", containsString("Product with id"));
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void updateRequiredQuantity_shouldReturn200_whenLinkExists() {
    ProductRawMaterialRequestDTO request =
        new ProductRawMaterialRequestDTO(rawMaterialId, new BigDecimal("1.00"));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(201);

    given()
        .contentType(ContentType.JSON)
        .body(new ProductRawMaterialRequestDTO(rawMaterialId, new BigDecimal("2.00")))
        .when()
        .put("/products/{productId}/raw-materials/{rawMaterialId}", productId, rawMaterialId)
        .then()
        .statusCode(200)
        .body("id", is(rawMaterialId.intValue()))
        .body("requiredQuantity", is(2.0f));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void updateRequiredQuantity_shouldReturn200_whenAdminRole() {
    ProductRawMaterialRequestDTO request =
        new ProductRawMaterialRequestDTO(rawMaterialId, new BigDecimal("1.00"));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(201);

    given()
        .contentType(ContentType.JSON)
        .body(new ProductRawMaterialRequestDTO(rawMaterialId, new BigDecimal("3.00")))
        .when()
        .put("/products/{productId}/raw-materials/{rawMaterialId}", productId, rawMaterialId)
        .then()
        .statusCode(200)
        .body("requiredQuantity", is(3.0f));
  }

  @Test
  void updateRequiredQuantity_shouldReturn401_whenAnonymous() {
    given()
        .contentType(ContentType.JSON)
        .body(new ProductRawMaterialRequestDTO(rawMaterialId, BigDecimal.ONE))
        .when()
        .put("/products/{productId}/raw-materials/{rawMaterialId}", productId, rawMaterialId)
        .then()
        .statusCode(401);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void updateRequiredQuantity_shouldReturn404_whenLinkDoesNotExist() {
    given()
        .contentType(ContentType.JSON)
        .body(new ProductRawMaterialRequestDTO(otherRawMaterialId, BigDecimal.ONE))
        .when()
        .put("/products/{productId}/raw-materials/{rawMaterialId}", productId, otherRawMaterialId)
        .then()
        .statusCode(404)
        .body("error", containsString("Raw material link not found"));
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void updateRequiredQuantity_shouldReturn404_whenProductDoesNotExist() {
    given()
        .contentType(ContentType.JSON)
        .body(new ProductRawMaterialRequestDTO(rawMaterialId, BigDecimal.ONE))
        .when()
        .put("/products/{productId}/raw-materials/{rawMaterialId}", 999999L, rawMaterialId)
        .then()
        .statusCode(404)
        .body("error", containsString("Product with id"));
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void updateRequiredQuantity_shouldReturn404_whenRawMaterialDoesNotExist() {
    given()
        .contentType(ContentType.JSON)
        .body(new ProductRawMaterialRequestDTO(999999L, BigDecimal.ONE))
        .when()
        .put("/products/{productId}/raw-materials/{rawMaterialId}", productId, 999999L)
        .then()
        .statusCode(404)
        .body("error", containsString("Raw material with id"));
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void updateRequiredQuantity_shouldReturn422_whenRequiredQuantityIsInvalid() {
    ProductRawMaterialRequestDTO request =
        new ProductRawMaterialRequestDTO(rawMaterialId, new BigDecimal("1.00"));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(201);

    given()
        .contentType(ContentType.JSON)
        .body(new ProductRawMaterialRequestDTO(rawMaterialId, BigDecimal.ZERO))
        .when()
        .put("/products/{productId}/raw-materials/{rawMaterialId}", productId, rawMaterialId)
        .then()
        .statusCode(422)
        .body("error", is("Invalid data"))
        .body("errors[0].field", is("requiredQuantity"));
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void remove_shouldReturn204_whenLinkExists() {
    ProductRawMaterialRequestDTO request =
        new ProductRawMaterialRequestDTO(rawMaterialId, new BigDecimal("1.00"));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(201);

    given()
        .when()
        .delete("/products/{productId}/raw-materials/{rawMaterialId}", productId, rawMaterialId)
        .then()
        .statusCode(204);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void remove_shouldReturn204_whenAdminRole() {
    ProductRawMaterialRequestDTO request =
        new ProductRawMaterialRequestDTO(rawMaterialId, new BigDecimal("1.00"));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(201);

    given()
        .when()
        .delete("/products/{productId}/raw-materials/{rawMaterialId}", productId, rawMaterialId)
        .then()
        .statusCode(204);
  }

  @Test
  void remove_shouldReturn401_whenAnonymous() {
    given()
        .when()
        .delete("/products/{productId}/raw-materials/{rawMaterialId}", productId, rawMaterialId)
        .then()
        .statusCode(401);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void remove_shouldReturn404_whenLinkDoesNotExist() {
    given()
        .when()
        .delete("/products/{productId}/raw-materials/{rawMaterialId}", productId, rawMaterialId)
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void remove_shouldReturn404_whenRawMaterialDoesNotExist() {
    given()
        .when()
        .delete("/products/{productId}/raw-materials/{rawMaterialId}", productId, 999999L)
        .then()
        .statusCode(404)
        .body("error", containsString("Raw material with id"));
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void remove_shouldReturn404_whenProductDoesNotExist() {
    given()
        .when()
        .delete("/products/{productId}/raw-materials/{rawMaterialId}", 999999L, rawMaterialId)
        .then()
        .statusCode(404)
        .body("error", containsString("Product with id"));
  }
}
