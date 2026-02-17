package org.autoflex.adapters.inbound.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import java.math.BigDecimal;
import java.util.List;
import org.autoflex.application.usecases.ProductionCapacityUseCase;
import org.autoflex.domain.ProductionCapacity;
import org.autoflex.domain.ProductionPlan;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestSecurity(
    user = "test-user",
    roles = {"ADMIN", "USER"})
public class ProductionCapacityResourceTest {

  @InjectMock ProductionCapacityUseCase productionCapacityUseCase;

  @Test
  void generate_shouldReturnProductionPlan_whenValidRequest() {
    ProductionPlan plan =
        new ProductionPlan(
            List.of(
                new ProductionCapacity(
                    1L,
                    "PROD-001",
                    "Dining Table",
                    new BigDecimal("1250.00"),
                    new BigDecimal("10"),
                    new BigDecimal("12500.00"))),
            new BigDecimal("12500.00"));

    when(productionCapacityUseCase.generate()).thenReturn(plan);

    given()
        .when()
        .get("/production-capacity")
        .then()
        .statusCode(200)
        .body("items", hasSize(1))
        .body("items[0].productId", is(1))
        .body("items[0].productCode", is("PROD-001"))
        .body("items[0].productName", is("Dining Table"))
        .body("items[0].unitPrice", is(1250.00f))
        .body("items[0].producibleQuantity", is(10))
        .body("items[0].totalValue", is(12500.00f))
        .body("grandTotalValue", is(12500.00f));

    verify(productionCapacityUseCase).generate();
  }

  @Test
  void generate_shouldReturnEmptyProductionPlan_whenNoCapacityAvailable() {
    ProductionPlan emptyPlan = new ProductionPlan(List.of(), BigDecimal.ZERO);

    when(productionCapacityUseCase.generate()).thenReturn(emptyPlan);

    given()
        .when()
        .get("/production-capacity")
        .then()
        .statusCode(200)
        .body("items", hasSize(0))
        .body("grandTotalValue", is(0));

    verify(productionCapacityUseCase).generate();
  }

  @Test
  @TestSecurity(
      user = "common-user",
      roles = {"USER"})
  void generate_shouldAllowUserRole_whenAuthenticatedAsUser() {
    when(productionCapacityUseCase.generate())
        .thenReturn(new ProductionPlan(List.of(), BigDecimal.ZERO));

    given().when().get("/production-capacity").then().statusCode(200);
  }

  @Test
  @TestSecurity(user = "")
  void generate_shouldReturn401_whenUserIsNotAuthenticated() {
    given().when().get("/production-capacity").then().statusCode(401);
  }

  @Test
  @TestSecurity(
      user = "guest-user",
      roles = {"GUEST"})
  void generate_shouldReturn403_whenUserHasNoAllowedRole() {
    given().when().get("/production-capacity").then().statusCode(403);
  }

  @Test
  void generate_shouldReturn500_whenUnexpectedErrorOccurs() {
    when(productionCapacityUseCase.generate()).thenThrow(new RuntimeException("unexpected error"));

    given().when().get("/production-capacity").then().statusCode(500);
  }
}
