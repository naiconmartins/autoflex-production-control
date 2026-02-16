package org.autoflex.web.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.List;
import org.autoflex.adapters.inbound.dto.request.ProductRawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.response.ProductRawMaterialResponseDTO;
import org.autoflex.application.services.ProductRawMaterialService;
import org.autoflex.factory.ProductRawMaterialFactory;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductRawMaterialResourceTest {

  @InjectMock ProductRawMaterialService service;

  @Test
  void add_shouldReturn401_whenUserIsNotAuthenticated() {
    ProductRawMaterialRequestDTO request =
        ProductRawMaterialFactory.createRequest(1L, BigDecimal.ONE);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/products/{productId}/raw-materials", 1L)
        .then()
        .statusCode(401);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void add_shouldReturn201_whenValidRequest() {
    Long productId = 1L;
    ProductRawMaterialRequestDTO request =
        ProductRawMaterialFactory.createRequest(2L, new BigDecimal("10.0"));
    ProductRawMaterialResponseDTO response =
        new ProductRawMaterialResponseDTO(2L, "RM-2", "Raw Material 2", request.requiredQuantity);

    when(service.add(eq(productId), any(ProductRawMaterialRequestDTO.class))).thenReturn(response);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(201)
        .body("id", is(2))
        .body("requiredQuantity", notNullValue());

    verify(service).add(eq(productId), any(ProductRawMaterialRequestDTO.class));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void add_shouldReturn201_whenAdminRole() {
    Long productId = 1L;
    ProductRawMaterialRequestDTO request =
        ProductRawMaterialFactory.createRequest(2L, new BigDecimal("10.0"));
    ProductRawMaterialResponseDTO response =
        new ProductRawMaterialResponseDTO(2L, "RM-2", "Raw Material 2", request.requiredQuantity);

    when(service.add(eq(productId), any(ProductRawMaterialRequestDTO.class))).thenReturn(response);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/products/{productId}/raw-materials", productId)
        .then()
        .statusCode(201)
        .body("id", is(2));
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void add_shouldReturn422_whenInvalidPayload() {
    ProductRawMaterialRequestDTO invalid = new ProductRawMaterialRequestDTO(null, null);

    given()
        .contentType(ContentType.JSON)
        .body(invalid)
        .when()
        .post("/products/{productId}/raw-materials", 1L)
        .then()
        .statusCode(422);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void add_shouldReturn409_whenConflictOccurs() {
    when(service.add(eq(1L), any(ProductRawMaterialRequestDTO.class)))
        .thenThrow(new ConflictException("Raw material already linked to product"));

    ProductRawMaterialRequestDTO request = new ProductRawMaterialRequestDTO(2L, BigDecimal.ONE);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/products/{productId}/raw-materials", 1L)
        .then()
        .statusCode(409);
  }

  @Test
  void list_shouldReturn401_whenUserIsNotAuthenticated() {
    given().when().get("/products/{productId}/raw-materials", 1L).then().statusCode(401);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void list_shouldReturn200_whenProductExists() {
    when(service.listByProduct(1L)).thenReturn(List.of());

    given()
        .when()
        .get("/products/{productId}/raw-materials", 1L)
        .then()
        .statusCode(200)
        .body("$", notNullValue());

    verify(service).listByProduct(1L);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void list_shouldReturn200_whenAdminRole() {
    when(service.listByProduct(1L)).thenReturn(List.of());

    given().when().get("/products/{productId}/raw-materials", 1L).then().statusCode(200);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void list_shouldReturn404_whenProductNotFound() {
    when(service.listByProduct(1L))
        .thenThrow(new ResourceNotFoundException("Product with id 1 not found"));

    given().when().get("/products/{productId}/raw-materials", 1L).then().statusCode(404);
  }

  @Test
  void updateRequiredQuantity_shouldReturn401_whenAnonymous() {
    given()
        .contentType(ContentType.JSON)
        .body(new ProductRawMaterialRequestDTO(2L, BigDecimal.ONE))
        .when()
        .put("/products/{productId}/raw-materials/{rawMaterialId}", 1L, 2L)
        .then()
        .statusCode(401);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void updateRequiredQuantity_shouldReturn200_whenValidRequest() {
    ProductRawMaterialRequestDTO request =
        new ProductRawMaterialRequestDTO(2L, new BigDecimal("2.00"));
    ProductRawMaterialResponseDTO response =
        new ProductRawMaterialResponseDTO(2L, "RM-2", "Raw Material 2", request.requiredQuantity);
    when(service.updateRequiredQuantity(eq(1L), eq(2L), any(ProductRawMaterialRequestDTO.class)))
        .thenReturn(response);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .put("/products/{productId}/raw-materials/{rawMaterialId}", 1L, 2L)
        .then()
        .statusCode(200)
        .body("requiredQuantity", is(2.0f));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void updateRequiredQuantity_shouldReturn422_whenInvalidPayload() {
    ProductRawMaterialRequestDTO invalid = new ProductRawMaterialRequestDTO(2L, BigDecimal.ZERO);

    given()
        .contentType(ContentType.JSON)
        .body(invalid)
        .when()
        .put("/products/{productId}/raw-materials/{rawMaterialId}", 1L, 2L)
        .then()
        .statusCode(422);

    verify(service, never())
        .updateRequiredQuantity(eq(1L), eq(2L), any(ProductRawMaterialRequestDTO.class));
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void updateRequiredQuantity_shouldReturn404_whenLinkNotFound() {
    ProductRawMaterialRequestDTO request = new ProductRawMaterialRequestDTO(2L, BigDecimal.ONE);
    when(service.updateRequiredQuantity(eq(1L), eq(2L), any(ProductRawMaterialRequestDTO.class)))
        .thenThrow(new ResourceNotFoundException("Raw material link not found for product 1"));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .put("/products/{productId}/raw-materials/{rawMaterialId}", 1L, 2L)
        .then()
        .statusCode(404);
  }

  @Test
  void remove_shouldReturn401_whenAnonymous() {
    given()
        .when()
        .delete("/products/{productId}/raw-materials/{rawMaterialId}", 1L, 2L)
        .then()
        .statusCode(401);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void remove_shouldReturn204_whenValidRequest() {
    given()
        .when()
        .delete("/products/{productId}/raw-materials/{rawMaterialId}", 1L, 2L)
        .then()
        .statusCode(204);

    verify(service).remove(1L, 2L);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void remove_shouldReturn404_whenLinkNotFound() {
    doThrow(new ResourceNotFoundException("Raw material link not found for product 1"))
        .when(service)
        .remove(1L, 2L);

    given()
        .when()
        .delete("/products/{productId}/raw-materials/{rawMaterialId}", 1L, 2L)
        .then()
        .statusCode(404);
  }
}
