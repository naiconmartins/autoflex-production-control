package org.autoflex.application.security;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.autoflex.adapters.inbound.dto.request.LoginRequestDTO;
import org.autoflex.domain.User;
import org.autoflex.domain.UserRole;
import org.junit.jupiter.api.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthResourceIT {

  @Inject PasswordService passwordService;

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
        .statusCode(401)
        .body("status", is(401))
        .body("error", is("Unauthorized"));
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
        .statusCode(401)
        .body("status", is(401))
        .body("error", is("Unauthorized"));
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
        .statusCode(401)
        .body("status", is(401))
        .body("error", is("Unauthorized"));
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
        .statusCode(422)
        .body("error", is("Invalid data"))
        .body("errors[0].field", is("email"));
  }

  @Test
  void login_shouldReturn422_whenEmailFormatIsInvalid() {
    LoginRequestDTO loginDto = new LoginRequestDTO("invalid-email", PASSWORD);

    given()
        .contentType(ContentType.JSON)
        .body(loginDto)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(422)
        .body("error", is("Invalid data"))
        .body("errors[0].field", is("email"));
  }

  @Test
  void login_shouldReturn422_whenPasswordIsBlank() {
    LoginRequestDTO loginDto = new LoginRequestDTO(EMAIL, "");

    given()
        .contentType(ContentType.JSON)
        .body(loginDto)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(422)
        .body("error", is("Invalid data"))
        .body("errors[0].field", is("password"));
  }

  @Test
  void login_shouldReturn422_whenEmailIsNull() {
    LoginRequestDTO loginDto = new LoginRequestDTO(null, PASSWORD);

    given()
        .contentType(ContentType.JSON)
        .body(loginDto)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(422)
        .body("error", is("Invalid data"));
  }

  @Test
  void login_shouldReturn422_whenPasswordIsNull() {
    LoginRequestDTO loginDto = new LoginRequestDTO(EMAIL, null);

    given()
        .contentType(ContentType.JSON)
        .body(loginDto)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(422)
        .body("error", is("Invalid data"));
  }

  @Test
  void login_shouldReturn422_whenBodyIsEmptyJson() {
    given()
        .contentType(ContentType.JSON)
        .body("{}")
        .when()
        .post("/auth/login")
        .then()
        .statusCode(422)
        .body("error", is("Invalid data"));
  }
}
