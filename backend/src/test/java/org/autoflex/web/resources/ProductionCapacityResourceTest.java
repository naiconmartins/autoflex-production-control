package org.autoflex.web.resources;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.autoflex.application.services.ProductionCapacityService;
import org.autoflex.web.dto.ProductionCapacityDTO;
import org.autoflex.web.dto.ProductionPlanResponseDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class ProductionCapacityResourceTest {

    @InjectMock
    ProductionCapacityService service;

    @Test
    void generate_shouldReturn401_whenUserIsNotAuthenticated() {
        given()
                .when()
                .get("/production-capacity")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void generate_shouldReturn200_whenAuthenticated() {
        ProductionPlanResponseDTO response = new ProductionPlanResponseDTO(
                List.of(new ProductionCapacityDTO(
                        1L,
                        "PROD-001",
                        "Test Product",
                        new BigDecimal("10.00"),
                        new BigDecimal("2"),
                        new BigDecimal("20.00")
                )),
                new BigDecimal("20.00")
        );

        when(service.generate()).thenReturn(response);

        given()
                .when()
                .get("/production-capacity")
                .then()
                .statusCode(200)
                .body("items", notNullValue())
                .body("items.size()", is(1))
                .body("grandTotalValue", notNullValue());

        verify(service).generate();
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void generate_shouldReturn200_whenAdminAuthenticated() {
        ProductionPlanResponseDTO response = new ProductionPlanResponseDTO(List.of(), BigDecimal.ZERO);
        when(service.generate()).thenReturn(response);

        given()
                .when()
                .get("/production-capacity")
                .then()
                .statusCode(200)
                .body("items", notNullValue())
                .body("items.size()", is(0))
                .body("grandTotalValue", notNullValue());

        verify(service).generate();
    }
}
