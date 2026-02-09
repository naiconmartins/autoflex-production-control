package org.autoflex.application.security;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.autoflex.domain.entities.User;
import org.autoflex.domain.enums.UserRole;
import org.autoflex.web.dto.LoginRequestDTO;
import org.junit.jupiter.api.*;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthResourceIT {

    @Inject
    PasswordService passwordService;

    static final String EMAIL = "auth.test@autoflex.com";
    static final String PASSWORD = "password123";

    @BeforeEach
    @Transactional
    void setUp() {
        User.delete("email", EMAIL);
        User user = new User();
        user.setEmail(EMAIL);
        user.setFirstName("Auth");
        user.setLastName("Test");
        user.setPasswordHash(passwordService.hash(PASSWORD));
        user.setActive(true);
        user.addRole(UserRole.USER);
        user.persist();
    }

    @Transactional
    void updateUserStatus(String email, boolean status) {
        User.update("active = ?1 where email = ?2", status, email);
    }

    @Test
    void login_shouldReturn200_whenCredentialsAreValid() {
        LoginRequestDTO loginDto = new LoginRequestDTO(EMAIL, PASSWORD);

        given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("expires", notNullValue());
    }

    @Test
    void login_shouldReturn401_whenPasswordIsWrong() {
        LoginRequestDTO loginDto = new LoginRequestDTO(EMAIL, "wrong_password");

        given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    void login_shouldReturn401_whenEmailDoesNotExist() {
        LoginRequestDTO loginDto = new LoginRequestDTO("nonexistent@autoflex.com", PASSWORD);

        given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    void login_shouldReturn401_whenUserIsInactive() {
        updateUserStatus(EMAIL, false);

        given()
                .contentType(ContentType.JSON)
                .body(new LoginRequestDTO(EMAIL, PASSWORD))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    void login_shouldReturn422_whenEmailIsBlank() {
        LoginRequestDTO loginDto = new LoginRequestDTO("", PASSWORD);

        given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(422);
    }
}