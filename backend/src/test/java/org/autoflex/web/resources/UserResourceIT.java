package org.autoflex.web.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.autoflex.domain.entities.User;
import org.autoflex.domain.enums.UserRole;
import org.autoflex.factory.UserFactory;
import org.autoflex.web.dto.UserRequestDTO;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserResourceIT {

    static String userEmail = "admin.it@autoflex.com";
    static final String INACTIVE_EMAIL = "inactive.it@autoflex.com";
    static final String MISSING_EMAIL = "missing.it@autoflex.com";

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
    @TestSecurity(user = MISSING_EMAIL, roles = "USER")
    void getCurrentUser_shouldReturn404_whenUserDoesNotExistInDatabase() {
        given()
                .when()
                .get("/user/me")
                .then()
                .statusCode(404)
                .body("error", containsString("User not found"));
    }

    @Test
    @Order(7)
    @TestSecurity(user = INACTIVE_EMAIL, roles = "USER")
    void getCurrentUser_shouldReturn401_whenUserIsInactive() {
        createOrReplaceUser(INACTIVE_EMAIL, false);

        given()
                .when()
                .get("/user/me")
                .then()
                .statusCode(401)
                .body("error", containsString("User is not active"));
    }

    @Test
    @Order(8)
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
    @Order(9)
    void getCurrentUser_shouldReturn401_whenAnonymous() {
        given()
                .when()
                .get("/user/me")
                .then()
                .statusCode(401);
    }

    @Test
    @Order(9)
    void insert_shouldReturn401_whenAnonymous() {
        UserRequestDTO request = UserFactory.createUserRequestDTO();

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/user")
                .then()
                .statusCode(401);
    }

    @Test
    @Order(10)
    @TestSecurity(user = "admin_system", roles = "ADMIN")
    void insert_shouldReturn422_whenPasswordIsBlank() {
        UserRequestDTO request = new UserRequestDTO(
                UserFactory.uniqueEmail(), "", "John", "Doe", "USER"
        );

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/user")
                .then()
                .statusCode(422)
                .body("error", is("Invalid data"))
                .body("errors[0].field", is("password"));
    }

    @Transactional
    void createOrReplaceUser(String email, boolean active) {
        User.delete("email", email);
        User user = new User();
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPasswordHash("hash");
        user.setActive(active);
        user.addRole(UserRole.USER);
        user.persist();
    }
}
