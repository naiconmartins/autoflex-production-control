package org.autoflex.adapters.inbound.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.autoflex.adapters.inbound.dto.request.LoginRequestDTO;
import org.autoflex.application.dto.TokenData;
import org.autoflex.application.usecases.AuthUseCase;
import org.autoflex.common.exceptions.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class AuthResourceTest {
  @InjectMock AuthUseCase authUseCase;

  private LoginRequestDTO dto;
  private TokenData tokenData;

  @BeforeEach
  void setUp() {
    dto = new LoginRequestDTO("email@test.com", "1234");
    tokenData = new TokenData("generated-jwt-token", 7200);
  }

  @Test
  void login_shouldReturn200_whenCredentialsAreValid() {
    when(authUseCase.authenticate(any())).thenReturn(tokenData);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(200)
        .body("accessToken", is("generated-jwt-token"))
        .body("expires", is(7200))
        .body("tokenType", is("Bearer"));
  }

  @Test
  void login_shouldReturn422_whenEmailIsNull() {
    dto.setEmail(null);
    when(authUseCase.authenticate(any())).thenReturn(tokenData);

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
    when(authUseCase.authenticate(any())).thenReturn(tokenData);

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
    when(authUseCase.authenticate(any())).thenReturn(tokenData);

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
    when(authUseCase.authenticate(any())).thenReturn(tokenData);

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
    when(authUseCase.authenticate(any())).thenReturn(tokenData);

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
    when(authUseCase.authenticate(any())).thenThrow(new UnauthorizedException());

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(401);
  }

  @Test
  void login_shouldReturn401_whenUserIsInactive() {
    when(authUseCase.authenticate(any()))
        .thenThrow(new UnauthorizedException("Invalid credentials"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(401)
        .body("error", is("Invalid credentials"));
  }
}
