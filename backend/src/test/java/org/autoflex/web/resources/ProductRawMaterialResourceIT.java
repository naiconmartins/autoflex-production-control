package org.autoflex.web.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.autoflex.web.dto.ProductRawMaterialRequestDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductRawMaterialResourceIT {

    static final Long PRODUCT_ID = 1L;
    static final Long RAW_MATERIAL_ID = 1L;

    @Test
    @Order(1)
    void add_shouldReturn401_whenAnonymous() {
        given()
                .contentType(ContentType.JSON)
                .body(new ProductRawMaterialRequestDTO(RAW_MATERIAL_ID, BigDecimal.ONE))
                .when()
                .post("/products/{productId}/raw-materials", PRODUCT_ID)
                .then()
                .statusCode(401);
    }

    @Test
    @Order(2)
    @TestSecurity(user = "user", roles = "USER")
    void add_shouldReturn201_whenValidRequest() {
        ProductRawMaterialRequestDTO request = new ProductRawMaterialRequestDTO(RAW_MATERIAL_ID, new BigDecimal("1.00"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/products/{productId}/raw-materials", PRODUCT_ID)
                .then()
                .statusCode(201)
                .body("id", is(RAW_MATERIAL_ID.intValue()))
                .body("requiredQuantity", notNullValue());
    }

    @Test
    @Order(3)
    @TestSecurity(user = "user", roles = "USER")
    void add_shouldReturn409_whenLinkAlreadyExists() {
        ProductRawMaterialRequestDTO request = new ProductRawMaterialRequestDTO(RAW_MATERIAL_ID, new BigDecimal("1.00"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/products/{productId}/raw-materials", PRODUCT_ID)
                .then()
                .statusCode(409);
    }

    @Test
    @Order(4)
    @TestSecurity(user = "user", roles = "USER")
    void list_shouldIncludeLinkedRawMaterial() {
        given()
                .when()
                .get("/products/{productId}/raw-materials", PRODUCT_ID)
                .then()
                .statusCode(200)
                .body("id", hasItem(RAW_MATERIAL_ID.intValue()));
    }

    @Test
    @Order(5)
    @TestSecurity(user = "user", roles = "USER")
    void updateRequiredQuantity_shouldReturn200_whenLinkExists() {
        ProductRawMaterialRequestDTO request = new ProductRawMaterialRequestDTO(RAW_MATERIAL_ID, new BigDecimal("2.00"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/products/{productId}/raw-materials/{rawMaterialId}", PRODUCT_ID, RAW_MATERIAL_ID)
                .then()
                .statusCode(200)
                .body("id", is(RAW_MATERIAL_ID.intValue()))
                .body("requiredQuantity", is(2.0f));
    }

    @Test
    @Order(6)
    @TestSecurity(user = "user", roles = "USER")
    void remove_shouldReturn204_whenLinkExists() {
        given()
                .when()
                .delete("/products/{productId}/raw-materials/{rawMaterialId}", PRODUCT_ID, RAW_MATERIAL_ID)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(7)
    @TestSecurity(user = "user", roles = "USER")
    void remove_shouldReturn404_whenLinkDoesNotExist() {
        given()
                .when()
                .delete("/products/{productId}/raw-materials/{rawMaterialId}", PRODUCT_ID, RAW_MATERIAL_ID)
                .then()
                .statusCode(404);
    }
}
