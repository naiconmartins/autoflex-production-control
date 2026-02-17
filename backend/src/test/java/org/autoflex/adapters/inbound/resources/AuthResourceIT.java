package org.autoflex.adapters.inbound.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.autoflex.adapters.inbound.dto.request.LoginRequestDTO;
import org.junit.jupiter.api.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthResourceIT {

  private LoginRequestDTO dto;

  @BeforeEach
  void setUp() {
    dto = new LoginRequestDTO("adm@autoflex.com", "adm");
  }

  @Test
  void login_shouldReturn200_whenCredentialsAreValid() {
    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(200)
        .body("accessToken", notNullValue())
        .body("expires", notNullValue());
  }

  @Test
  void login_shouldReturn200_whenCredentialsAreInvalid() {
    dto.setEmail("tax@autoflex");
    dto.setPassword("1234");

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(401);
  }

  @Test
  void login_shouldReturn422_whenEmailIsNull() {
    dto.setEmail(null);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("email"))
        .body("errors[0].message", is("Email is required"));
  }

  @Test
  void login_shouldReturn422_whenEmailIsBlank() {
    dto.setEmail("");

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("email"))
        .body("errors[0].message", is("Email is required"));
  }

  @Test
  void login_shouldReturn422_whenEmailIsInvalid() {
    dto.setEmail("email");

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("email"))
        .body("errors[0].message", is("Invalid email format"));
  }

  @Test
  void login_shouldReturn422_whenPasswordIsNull() {
    dto.setPassword(null);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("password"))
        .body("errors[0].message", is("Password is required"));
  }

  @Test
  void login_shouldReturn422_whenPasswordIsBlank() {
    dto.setPassword("");

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("password"))
        .body("errors[0].message", is("Password is required"));
  }

  @Test
  void login_shouldReturn401_whenPasswordIsInvalid() {
    dto.setPassword("invalid");

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(401);
  }
}
