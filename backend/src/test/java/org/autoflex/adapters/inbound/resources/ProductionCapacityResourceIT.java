package org.autoflex.adapters.inbound.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductionCapacityResourceIT {

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void generate_shouldReturnProductionPlan_whenAuthenticatedAsAdmin() {
    given()
        .when()
        .get("/production-capacity")
        .then()
        .statusCode(200)
        .body("items", hasSize(greaterThan(0)))
        .body("items[0].productId", notNullValue())
        .body("items[0].productCode", notNullValue())
        .body("items[0].productName", notNullValue())
        .body("items[0].unitPrice", greaterThan(0f))
        .body("items[0].producibleQuantity", greaterThan(0))
        .body("items[0].totalValue", greaterThan(0f))
        .body("grandTotalValue", greaterThan(0f));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void generate_shouldReturnConsistentTotals_whenAuthenticatedAsAdmin() {
    Response response = given().when().get("/production-capacity");

    response.then().statusCode(200).body("items", hasSize(greaterThan(0)));

    Float firstItemTotal = response.then().extract().path("items[0].totalValue");
    Float grandTotal = response.then().extract().path("grandTotalValue");

    Assertions.assertNotNull(firstItemTotal);
    Assertions.assertNotNull(grandTotal);
    Assertions.assertTrue(grandTotal >= firstItemTotal);
  }

  @Test
  @TestSecurity(user = "user", roles = "USER")
  void generate_shouldReturnProductionPlan_whenAuthenticatedAsUser() {
    given()
        .when()
        .get("/production-capacity")
        .then()
        .statusCode(200)
        .body("items", hasSize(greaterThan(0)))
        .body("grandTotalValue", greaterThan(0f));
  }

  @Test
  void generate_shouldReturn401_whenUserIsNotAuthenticated() {
    given().when().get("/production-capacity").then().statusCode(401);
  }

  @Test
  @TestSecurity(user = "guest", roles = "GUEST")
  void generate_shouldReturn403_whenUserHasNoAllowedRole() {
    given().when().get("/production-capacity").then().statusCode(403);
  }
}
