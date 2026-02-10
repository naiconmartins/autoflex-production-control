package org.autoflex.web.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class ProductionCapacityResourceIT {

    @Test
    void generate_shouldReturn401_whenAnonymous() {
        given()
                .when()
                .get("/production-capacity")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void generate_shouldReturn200AndPlan_whenAuthenticated() {
        given()
                .when()
                .get("/production-capacity")
                .then()
                .statusCode(200)
                .body("items", notNullValue())
                .body("grandTotalValue", notNullValue());
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void generate_shouldReturn200AndPlan_whenAdminAuthenticated() {
        given()
                .when()
                .get("/production-capacity")
                .then()
                .statusCode(200)
                .body("items", notNullValue())
                .body("grandTotalValue", notNullValue());
    }
}
