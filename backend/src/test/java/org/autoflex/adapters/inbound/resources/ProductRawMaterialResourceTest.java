package org.autoflex.adapters.inbound.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.List;
import org.autoflex.adapters.inbound.dto.request.ProductRawMaterialRequestDTO;
import org.autoflex.application.commands.ProductRawMaterialCommand;
import org.autoflex.application.usecases.ProductRawMaterialUseCase;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.domain.ProductRawMaterial;
import org.autoflex.fixtures.ProductRawMaterialFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestSecurity(
    user = "test-user",
    roles = {"ADMIN", "USER"})
public class ProductRawMaterialResourceTest {

  @InjectMock ProductRawMaterialUseCase service;

  private Long productId;
  private Long rawMaterialId;
  private ProductRawMaterialRequestDTO dto;
  private ProductRawMaterial link;

  @BeforeEach
  void setUp() {
    productId = 1L;
    rawMaterialId = 2L;
    dto = new ProductRawMaterialRequestDTO(rawMaterialId, new BigDecimal("10.00"));
    link =
        ProductRawMaterialFixture.createLink(10L, productId, rawMaterialId, dto.requiredQuantity);
  }

  @Test
  void add_shouldCreateAssociation_whenValidRequest() {
    when(service.add(eq(productId), any(ProductRawMaterialCommand.class))).thenReturn(link);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(201)
        .body("id", is(rawMaterialId.intValue()))
        .body("code", is(link.getRawMaterial().getCode()))
        .body("name", is(link.getRawMaterial().getName()))
        .body("requiredQuantity", is(10.00f));
  }

  @Test
  void add_shouldReturn422_whenRawMaterialIdIsNull() {
    dto.id = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("id"))
        .body("errors[0].message", is("Raw material id is required"));
  }

  @Test
  void add_shouldReturn422_whenRequiredQuantityIsNull() {
    dto.requiredQuantity = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("requiredQuantity"))
        .body("errors[0].message", is("Required quantity is required"));
  }

  @Test
  void add_shouldReturn422_whenRequiredQuantityIsZero() {
    dto.requiredQuantity = BigDecimal.ZERO;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("requiredQuantity"))
        .body("errors[0].message", is("Required quantity must be greater than zero"));
  }

  @Test
  void add_shouldReturn404_whenProductNotExists() {
    when(service.add(eq(productId), any(ProductRawMaterialCommand.class)))
        .thenThrow(new ResourceNotFoundException("Product with id 1 not found"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(404)
        .body("error", is("Product with id 1 not found"));
  }

  @Test
  void add_shouldReturn404_whenRawMaterialNotExists() {
    when(service.add(eq(productId), any(ProductRawMaterialCommand.class)))
        .thenThrow(new ResourceNotFoundException("Raw material with id 2 not found"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(404)
        .body("error", is("Raw material with id 2 not found"));
  }

  @Test
  void add_shouldReturn409_whenAssociationAlreadyExists() {
    when(service.add(eq(productId), any(ProductRawMaterialCommand.class)))
        .thenThrow(new ConflictException("Raw material already linked to product"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(409)
        .body("error", is("Raw material already linked to product"));
  }

  @Test
  void list_shouldReturnAssociations_whenProductExists() {
    when(service.listByProduct(productId)).thenReturn(List.of(link));

    given()
        .when()
        .get("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(200)
        .body("size()", is(1))
        .body("id", hasSize(1))
        .body("id[0]", is(rawMaterialId.intValue()))
        .body("requiredQuantity[0]", is(10.00f));
  }

  @Test
  void list_shouldReturnEmptyList_whenNoAssociations() {
    when(service.listByProduct(productId)).thenReturn(List.of());

    given()
        .when()
        .get("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(200)
        .body("size()", is(0));
  }

  @Test
  void list_shouldReturn404_whenProductNotExists() {
    when(service.listByProduct(productId))
        .thenThrow(new ResourceNotFoundException("Product with id 1 not found"));

    given()
        .when()
        .get("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(404)
        .body("error", is("Product with id 1 not found"));
  }

  @Test
  void updateRequiredQuantity_shouldUpdateAssociation_whenValidRequest() {
    when(service.updateRequiredQuantity(
            eq(productId), eq(rawMaterialId), any(ProductRawMaterialCommand.class)))
        .thenReturn(link);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{productId}/raw-materials/{rawMaterialId}", productId, rawMaterialId)
        .then()
        .statusCode(200)
        .body("id", is(rawMaterialId.intValue()))
        .body("requiredQuantity", is(10.00f));
  }

  @Test
  void updateRequiredQuantity_shouldReturn422_whenRequiredQuantityIsNull() {
    dto.requiredQuantity = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{productId}/raw-materials/{rawMaterialId}", productId, rawMaterialId)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("requiredQuantity"))
        .body("errors[0].message", is("Required quantity is required"));
  }

  @Test
  void updateRequiredQuantity_shouldReturn404_whenAssociationNotExists() {
    when(service.updateRequiredQuantity(
            eq(productId), eq(rawMaterialId), any(ProductRawMaterialCommand.class)))
        .thenThrow(
            new ResourceNotFoundException(
                "Association not found between Product 1 and Raw Material 2"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{productId}/raw-materials/{rawMaterialId}", productId, rawMaterialId)
        .then()
        .statusCode(404)
        .body("error", is("Association not found between Product 1 and Raw Material 2"));
  }

  @Test
  void remove_shouldDeleteAssociation_whenValidRequest() {
    given()
        .when()
        .delete("/products/{productId}/raw-materials/{rawMaterialId}", productId, rawMaterialId)
        .then()
        .statusCode(204);
  }

  @Test
  void remove_shouldReturn404_whenAssociationNotExists() {
    doThrow(
            new ResourceNotFoundException(
                "Association not found between Product ID 1 and Raw Material ID 2"))
        .when(service)
        .remove(productId, rawMaterialId);

    given()
        .when()
        .delete("/products/{productId}/raw-materials/{rawMaterialId}", productId, rawMaterialId)
        .then()
        .statusCode(404)
        .body("error", is("Association not found between Product ID 1 and Raw Material ID 2"));
  }

  @Test
  @TestSecurity(user = "")
  void list_shouldReturn401_whenUserIsNotAuthenticated() {
    given().when().get("/products/{productId}/raw-materials", productId).then().statusCode(401);
  }
}
