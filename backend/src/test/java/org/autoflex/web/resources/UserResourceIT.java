package org.autoflex.web.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.autoflex.factory.UserFactory;
import org.autoflex.web.dto.UserRequestDTO;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserResourceIT {

    static String userEmail = "admin.it@autoflex.com";

    @Test
    @Order(1)
    @TestSecurity(user = "admin_system", roles = "ADMIN")
    void insert_shouldPersistUser_whenAdmin() {
        UserRequestDTO request = new UserRequestDTO(
                userEmail, "pass123", "Admin", "IT", "ADMIN"
        );

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/user")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .body("email", is(userEmail))
                .body("roles", hasItem("ADMIN"));
    }

    @Test
    @Order(2)
    @TestSecurity(user = "admin_system", roles = "ADMIN")
    void insert_shouldReturn409_whenEmailExists() {
        UserRequestDTO request = new UserRequestDTO(
                userEmail, "other", "Other", "Name", "USER"
        );

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/user")
                .then()
                .statusCode(409)
                .body("error", containsString("already exists"));
    }

    @Test
    @Order(3)
    @TestSecurity(user = "admin_system", roles = "ADMIN")
    void insert_shouldReturn422_whenRoleIsInvalid() {
        UserRequestDTO request = new UserRequestDTO(
                "invalid-role@test.com", "123", "Test", "Role", "GOD_MODE"
        );

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/user")
                .then()
                .statusCode(422)
                .body("error", containsString("Invalid role: GOD_MODE"));
    }

    @Test
    @Order(4)
    @TestSecurity(user = "admin_system", roles = "ADMIN")
    void insert_shouldReturn422_whenEmailIsInvalid() {
        UserRequestDTO request = new UserRequestDTO(
                "email-without-at", "123", "John", "Doe", "USER"
        );

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/user")
                .then()
                .statusCode(422);
    }

    @Test
    @Order(5)
    @TestSecurity(user = "admin.it@autoflex.com", roles = "ADMIN")
    void getCurrentUser_shouldReturnLoggedUserInfo() {
        given()
                .when()
                .get("/user/me")
                .then()
                .statusCode(200)
                .body("email", is(userEmail))
                .body("active", is(true));
    }

    @Test
    @Order(6)
    @TestSecurity(user = "common@test.com", roles = "USER")
    void insert_shouldReturn403_whenNotAdmin() {
        UserRequestDTO request = UserFactory.createUserRequestDTO();

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/user")
                .then()
                .statusCode(403);
    }

    @Test
    @Order(7)
    void getCurrentUser_shouldReturn401_whenAnonymous() {
        given()
                .when()
                .get("/user/me")
                .then()
                .statusCode(401);
    }
}