package org.autoflex.adapters.inbound.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
import org.autoflex.adapters.inbound.dto.request.ProductRawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.request.ProductRequestDTO;
import org.autoflex.application.commands.ProductCommand;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.application.dto.SearchQuery;
import org.autoflex.application.usecases.ProductUseCase;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.DatabaseException;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.domain.Product;
import org.autoflex.domain.RawMaterial;
import org.autoflex.fixtures.ProductFixture;
import org.autoflex.fixtures.RawMaterialFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestSecurity(user = "test-user", roles = {"ADMIN", "USER"})
public class ProductResourceTest {

  @InjectMock ProductUseCase productUseCase;

  private ProductRequestDTO dto;
  private Product product;
  private Long existingId;

  @BeforeEach
  void setUp() {
    dto = createValidRequest();
    existingId = 1L;
    product = createProductForResponse(existingId);
  }

  @Test
  void insert_shouldCreateProduct_whenValidRequest() {
    when(productUseCase.insert(any(ProductCommand.class))).thenReturn(product);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products")
        .then()
        .statusCode(201)
        .header("Location", endsWith("/products/1"))
        .body("id", is(1))
        .body("code", is(dto.code))
        .body("name", is(dto.name))
        .body("rawMaterials", hasSize(1))
        .body("rawMaterials[0].id", is(1))
        .body("rawMaterials[0].requiredQuantity", is(150.00f));
  }

  @Test
  void insert_shouldReturn422_whenRawMaterialsIsNull() {
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
  void insert_shouldReturn422_whenRawMaterialsIsEmpty() {
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
  void insert_shouldReturn422_whenCodeIsNull() {
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
  void insert_shouldReturn422_whenCodeIsEmpty() {
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
  void insert_shouldReturn422_whenNameIsNull() {
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
  void insert_shouldReturn422_whenNameIsEmpty() {
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
  void insert_shouldReturn422_whenPriceIsNull() {
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
  void insert_shouldReturn422_whenPriceIsZero() {
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
  void insert_shouldReturn422_whenPriceIsNegative() {
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
  void insert_shouldReturn422_whenRawMaterialIdIsNull() {
    dto.rawMaterials = List.of(new ProductRawMaterialRequestDTO(null, new BigDecimal("150.00")));

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
  void insert_shouldReturn422_whenRawMaterialRequiredQuantityIsNull() {
    dto.rawMaterials = List.of(new ProductRawMaterialRequestDTO(1L, null));

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
  void insert_shouldReturn422_whenRawMaterialRequiredQuantityIsZero() {
    dto.rawMaterials = List.of(new ProductRawMaterialRequestDTO(1L, BigDecimal.ZERO));

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
  void insert_shouldReturn409_whenCodeAlreadyExist() {
    when(productUseCase.insert(any(ProductCommand.class)))
        .thenThrow(new ConflictException("Product code already exists"));

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
  void insert_shouldReturn404_whenRawMaterialNotExist() {
    when(productUseCase.insert(any(ProductCommand.class)))
        .thenThrow(new ResourceNotFoundException("Raw Material not found: 1"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/products")
        .then()
        .statusCode(404)
        .body("error", is("Raw Material not found: 1"));
  }

  @Test
  void update_shouldUpdateProduct_whenValidRequest() {
    when(productUseCase.update(eq(existingId), any(ProductCommand.class))).thenReturn(product);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", existingId)
        .then()
        .statusCode(200)
        .body("id", is(1))
        .body("code", is(dto.code))
        .body("name", is(dto.name))
        .body("rawMaterials", hasSize(1))
        .body("rawMaterials[0].id", is(1));
  }

  @Test
  void update_shouldReturn404_whenIdNotExist() {
    when(productUseCase.update(eq(2L), any(ProductCommand.class)))
        .thenThrow(new ResourceNotFoundException("Product with id 2 not found"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", 2L)
        .then()
        .statusCode(404)
        .body("error", is("Product with id 2 not found"));
  }

  @Test
  void update_shouldReturn409_whenCodeAlreadyExist() {
    when(productUseCase.update(eq(existingId), any(ProductCommand.class)))
        .thenThrow(new ConflictException("Product code already exists"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", existingId)
        .then()
        .statusCode(409)
        .body("error", is("Product code already exists"));
  }

  @Test
  void update_shouldUpdateProduct_whenCodeBelongsToSameProduct() {
    when(productUseCase.update(eq(existingId), any(ProductCommand.class))).thenReturn(product);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", existingId)
        .then()
        .statusCode(200)
        .body("id", is(1))
        .body("code", is(dto.code));
  }

  @Test
  void update_shouldReturn422_whenRawMaterialsIsNull() {
    dto.rawMaterials = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", existingId)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("rawMaterials"))
        .body("errors[0].message", is("At least one raw material is required"));
  }

  @Test
  void update_shouldReturn422_whenCodeIsNull() {
    dto.code = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", existingId)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("code"))
        .body("errors[0].message", is("Product code is required"));
  }

  @Test
  void update_shouldReturn422_whenNameIsNull() {
    dto.name = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", existingId)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("name"))
        .body("errors[0].message", is("Product name is required"));
  }

  @Test
  void update_shouldReturn422_whenPriceIsNull() {
    dto.price = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", existingId)
        .then()
        .statusCode(422)
        .body("errors[0].field", is("price"))
        .body("errors[0].message", is("Product price is required"));
  }

  @Test
  void update_shouldReturn404_whenRawMaterialNotExist() {
    when(productUseCase.update(eq(existingId), any(ProductCommand.class)))
        .thenThrow(new ResourceNotFoundException("Raw Material not found: 1"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .put("/products/{id}", existingId)
        .then()
        .statusCode(404)
        .body("error", is("Raw Material not found: 1"));
  }

  @Test
  void delete_shouldDeleteProduct_whenValidRequest() {
    given().when().delete("/products/{id}", existingId).then().statusCode(204);
  }

  @Test
  void delete_shouldReturn404_whenIdNotExist() {
    doThrow(new ResourceNotFoundException("Product with id 1 not found"))
        .when(productUseCase)
        .delete(existingId);

    given()
        .when()
        .delete("/products/{id}", existingId)
        .then()
        .statusCode(404)
        .body("error", is("Product with id 1 not found"));
  }

  @Test
  void delete_shouldReturn400_whenDeleteFails() {
    doThrow(new DatabaseException("Cannot delete product because it is referenced by other records"))
        .when(productUseCase)
        .delete(existingId);

    given()
        .when()
        .delete("/products/{id}", existingId)
        .then()
        .statusCode(400)
        .body("error", is("Cannot delete product because it is referenced by other records"));
  }

  @Test
  @TestSecurity(user = "test-user", roles = {"USER"})
  void delete_shouldReturn403_whenUserIsNotAdmin() {
    given().when().delete("/products/{id}", existingId).then().statusCode(403);
  }

  @Test
  @TestSecurity(user = "")
  void findAll_shouldReturn401_whenUserIsNotAuthenticated() {
    given().when().get("/products").then().statusCode(401);
  }

  @Test
  void findAll_shouldReturnAllProducts_whenValidRequest() {
    PagedModel<Product> paged = new PagedModel<>(List.of(product), 1L, 1);
    when(productUseCase.findAll(any())).thenReturn(paged);

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
        .body("totalPages", is(1))
        .body("content", hasSize(1))
        .body("content[0].id", is(1))
        .body("content[0].code", is(dto.code));
  }

  @Test
  void findAll_shouldReturnEmptyPage_whenNoProducts() {
    when(productUseCase.findAll(any())).thenReturn(new PagedModel<>(List.of(), 0L, 0));

    given()
        .queryParam("page", 0)
        .queryParam("size", 10)
        .queryParam("sort", "name")
        .queryParam("dir", "asc")
        .when()
        .get("/products")
        .then()
        .statusCode(200)
        .body("totalElements", is(0))
        .body("totalPages", is(0))
        .body("content", hasSize(0));
  }

  @Test
  void findAll_shouldUseDefaultPagination_whenQueryParamsAreNotProvided() {
    when(productUseCase.findAll(any())).thenReturn(new PagedModel<>(List.of(), 0L, 0));

    given().when().get("/products").then().statusCode(200);

    verify(productUseCase).findAll(eq(new SearchQuery(0, 10, "name", "asc")));
  }

  @Test
  void findById_shouldReturnProduct_whenIdExists() {
    when(productUseCase.findById(existingId)).thenReturn(product);

    given()
        .when()
        .get("/products/{id}", existingId)
        .then()
        .statusCode(200)
        .body("id", is(1))
        .body("code", is(dto.code))
        .body("name", is(dto.name))
        .body("rawMaterials", hasSize(1));
  }

  @Test
  void findById_shouldReturn404_whenIdNotExist() {
    when(productUseCase.findById(existingId))
        .thenThrow(new ResourceNotFoundException("Product not found with id: 1"));

    given()
        .when()
        .get("/products/{id}", existingId)
        .then()
        .statusCode(404)
        .body("error", is("Product not found with id: 1"));
  }

  @Test
  void findByName_shouldReturnProducts_whenValidRequest() {
    PagedModel<Product> paged = new PagedModel<>(List.of(product), 1L, 1);
    when(productUseCase.findByName(eq("Dining"), any())).thenReturn(paged);

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
        .body("totalElements", is(1))
        .body("totalPages", is(1))
        .body("content", hasSize(1))
        .body("content[0].name", is(dto.name));
  }

  @Test
  void findByName_shouldReturnEmptyPage_whenNoMatch() {
    when(productUseCase.findByName(eq("NotFound"), any()))
        .thenReturn(new PagedModel<>(List.of(), 0L, 0));

    given()
        .queryParam("name", "NotFound")
        .queryParam("page", 0)
        .queryParam("size", 10)
        .queryParam("sort", "name")
        .queryParam("dir", "asc")
        .when()
        .get("/products/search")
        .then()
        .statusCode(200)
        .body("totalElements", is(0))
        .body("totalPages", is(0))
        .body("content", hasSize(0));
  }

  @Test
  void findByName_shouldUseDefaultPagination_whenQueryParamsAreNotProvided() {
    when(productUseCase.findByName(eq("Dining"), any()))
        .thenReturn(new PagedModel<>(List.of(), 0L, 0));

    given().queryParam("name", "Dining").when().get("/products/search").then().statusCode(200);

    verify(productUseCase).findByName(eq("Dining"), eq(new SearchQuery(0, 10, "name", "asc")));
  }

  private static ProductRequestDTO createValidRequest() {
    ProductCommand cmd = ProductFixture.createValidProductCommand();
    ProductCommand.RawMaterialItem item = cmd.rawMaterials().getFirst();
    ProductRawMaterialRequestDTO rawItem =
        new ProductRawMaterialRequestDTO(item.rawMaterialId(), item.requiredQuantity());

    return new ProductRequestDTO(cmd.code(), cmd.name(), cmd.price(), List.of(rawItem));
  }

  private static Product createProductForResponse(Long id) {
    ProductCommand cmd = ProductFixture.createValidProductCommand();
    Product p = new Product(cmd.code(), cmd.name(), cmd.price());
    p.setId(id);
    RawMaterial rawMaterial = RawMaterialFixture.createRawMaterial(1L);
    rawMaterial.setCode("RAW001");
    rawMaterial.setName("MDF Board");
    p.addRawMaterial(rawMaterial, new BigDecimal("150.00"));
    return p;
  }
}
