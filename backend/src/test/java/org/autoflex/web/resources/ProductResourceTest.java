package org.autoflex.web.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
import org.autoflex.adapters.inbound.dto.request.PageRequestDTO;
import org.autoflex.adapters.inbound.dto.request.ProductRawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.request.ProductRequestDTO;
import org.autoflex.adapters.inbound.dto.response.PageResponseDTO;
import org.autoflex.adapters.inbound.dto.response.ProductResponseDTO;
import org.autoflex.application.services.ProductServiceImpl;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.DatabaseException;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.factory.ProductFactory;
import org.autoflex.factory.ProductRawMaterialFactory;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductResourceTest {

  @InjectMock
  ProductServiceImpl productServiceImpl;

  @Test
  void insert_shouldReturn401_whenUserIsNotAuthenticated() {
    ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/products")
        .then()
        .statusCode(401);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn201_whenDataIsValid() {
    ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();
    ProductResponseDTO response = ProductFactory.createProductResponseDTOWithRawMaterials();

    when(productServiceImpl.insert(any(ProductRequestDTO.class))).thenReturn(response);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/products")
        .then()
        .statusCode(201)
        .header("Location", org.hamcrest.Matchers.containsString("/products/1"))
        .body("code", is("PROD001"));
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void insert_shouldReturn201_whenAuthenticatedUserHasUserRole() {
    ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();
    ProductResponseDTO response = ProductFactory.createProductResponseDTOWithRawMaterials();

    when(productServiceImpl.insert(any(ProductRequestDTO.class))).thenReturn(response);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/products")
        .then()
        .statusCode(201)
        .body("code", is("PROD001"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenRawMaterialsIsEmpty() {
    ProductRequestDTO request = ProductFactory.createProductRequestDTO();

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/products")
        .then()
        .statusCode(422);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenFieldsIsIsEmpty() {
    List<ProductRawMaterialRequestDTO> productRawMaterial =
        ProductRawMaterialFactory.createListOfRawMaterialsRequestDTO();

    ProductRequestDTO request =
        new ProductRequestDTO("", "", new BigDecimal("10.0"), productRawMaterial);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/products")
        .then()
        .statusCode(422);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn409_whenCodeAlreadyExists() {
    ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

    when(productServiceImpl.insert(any(ProductRequestDTO.class)))
        .thenThrow(new ConflictException("Product code already exists"));

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
  void insert_shouldReturn404_whenRawMaterialNotFound() {
    ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

    when(productServiceImpl.insert(any(ProductRequestDTO.class)))
        .thenThrow(new ResourceNotFoundException("Raw material not found"));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/products")
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn200_whenDataIsValid() {
    Long id = 1L;
    ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();
    ProductResponseDTO response = ProductFactory.createProductResponseDTOWithRawMaterials();

    when(productServiceImpl.update(eq(id), any(ProductRequestDTO.class))).thenReturn(response);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .put("/products/{id}", id)
        .then()
        .statusCode(200)
        .body("id", is(1))
        .body("name", is(response.name));
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void update_shouldReturn200_whenAuthenticatedUserHasUserRole() {
    Long id = 1L;
    ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();
    ProductResponseDTO response = ProductFactory.createProductResponseDTOWithRawMaterials();

    when(productServiceImpl.update(eq(id), any(ProductRequestDTO.class))).thenReturn(response);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .put("/products/{id}", id)
        .then()
        .statusCode(200)
        .body("id", is(1))
        .body("name", is(response.name));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn422_whenDataIsInvalid() {
    ProductRequestDTO invalidRequest =
        new ProductRequestDTO("ERR-01", "", new BigDecimal("-1.0"), List.of());

    given()
        .contentType(ContentType.JSON)
        .body(invalidRequest)
        .when()
        .put("/products/{id}", 1L)
        .then()
        .statusCode(422);

    verify(productServiceImpl, never()).update(eq(1L), any(ProductRequestDTO.class));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn409_whenCodeAlreadyExistsInAnotherProduct() {
    Long id = 1L;
    ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

    when(productServiceImpl.update(eq(id), any(ProductRequestDTO.class)))
        .thenThrow(new ConflictException("Product code already exists"));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .put("/products/{id}", id)
        .then()
        .statusCode(409);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn404_whenRawMaterialNotFound() {
    Long id = 1L;
    ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

    when(productServiceImpl.update(eq(id), any(ProductRequestDTO.class)))
        .thenThrow(new ResourceNotFoundException("Raw material not found"));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .put("/products/{id}", id)
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void update_shouldReturn404_whenIdDoesNotExist() {
    Long invalidId = 99L;
    ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

    when(productServiceImpl.update(eq(invalidId), any(ProductRequestDTO.class)))
        .thenThrow(new ResourceNotFoundException("Product not found"));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .put("/products/{id}", invalidId)
        .then()
        .statusCode(404);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void delete_shouldReturn204_whenIdExists() {
    Long id = 1L;

    given().when().delete("/products/{id}", id).then().statusCode(204);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void delete_shouldReturn403_whenUserIsNotAdmin() {
    given().when().delete("/products/1").then().statusCode(403);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void delete_shouldReturn400_whenDatabaseExceptionOccurs() {
    Long id = 1L;

    doThrow(new DatabaseException("Cannot delete product...")).when(productServiceImpl).delete(id);

    given().when().delete("/products/{id}", id).then().statusCode(400);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void findById_shouldReturn200_whenIdExists() {
    Long id = 1L;
    ProductResponseDTO response = ProductFactory.createProductResponseDTOWithRawMaterials();

    when(productServiceImpl.findById(id)).thenReturn(response);

    given()
        .when()
        .get("/products/{id}", id)
        .then()
        .statusCode(200)
        .body("id", is(1))
        .body("code", is("PROD001"));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void findById_shouldReturn404_whenIdDoesNotExist() {
    Long invalidId = 99L;

    when(productServiceImpl.findById(invalidId))
        .thenThrow(new ResourceNotFoundException("Product with id " + invalidId + " not found"));

    given().when().get("/products/{id}", invalidId).then().statusCode(404);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void findAll_shouldReturnPageResponse_whenCalled() {
    ProductResponseDTO p1 = ProductFactory.createProductResponseDTOWithRawMaterials();

    PageResponseDTO<ProductResponseDTO> pageResponse =
        new PageResponseDTO<>(List.of(p1), 1L, 1, 0, 10);

    when(productServiceImpl.findAll(any(PageRequestDTO.class))).thenReturn(pageResponse);

    given()
        .queryParam("page", 0)
        .queryParam("size", 10)
        .queryParam("sort", "name")
        .queryParam("dir", "asc")
        .when()
        .get("/products")
        .then()
        .statusCode(200)
        .body("totalElements", is(1))
        .body("content[0].code", is("PROD001"));
  }

  @Test
  void update_shouldReturn401_whenUserIsNotAuthenticated() {
    ProductRequestDTO request = ProductFactory.createProductRequestDTOWithRawMaterials();

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .put("/products/{id}", 1L)
        .then()
        .statusCode(401);
  }

  @Test
  void delete_shouldReturn401_whenUserIsNotAuthenticated() {
    given().when().delete("/products/{id}", 1L).then().statusCode(401);
  }

  @Test
  void findById_shouldReturn401_whenUserIsNotAuthenticated() {
    given().when().get("/products/{id}", 1L).then().statusCode(401);
  }

  @Test
  void findAll_shouldReturn401_whenUserIsNotAuthenticated() {
    given().when().get("/products").then().statusCode(401);
  }

  @Test
  void findByName_shouldReturn401_whenUserIsNotAuthenticated() {
    given().queryParam("name", "steel").when().get("/products/search").then().statusCode(401);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void findByName_shouldReturnPagedResponse_whenCalled() {
    ProductResponseDTO p1 = ProductFactory.createProductResponseDTOWithRawMaterials();
    PageResponseDTO<ProductResponseDTO> pageResponse =
        new PageResponseDTO<>(List.of(p1), 1L, 1, 1, 5);

    when(productServiceImpl.findByName(eq("steel"), any(PageRequestDTO.class)))
        .thenReturn(pageResponse);

    given()
        .queryParam("name", "steel")
        .queryParam("page", 1)
        .queryParam("size", 5)
        .queryParam("sort", "name")
        .queryParam("dir", "asc")
        .when()
        .get("/products/search")
        .then()
        .statusCode(200)
        .body("totalElements", is(1))
        .body("content[0].code", is("PROD001"))
        .body("page", is(1))
        .body("size", is(5));

    verify(productServiceImpl)
        .findByName(
            eq("steel"),
            argThat(
                dto ->
                    dto.page == 1
                        && dto.size == 5
                        && "name".equals(dto.sortBy)
                        && "asc".equals(dto.direction)));
  }
}
