package org.autoflex.application.security;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.autoflex.adapters.inbound.dto.request.LoginRequestDTO;
import org.autoflex.adapters.inbound.dto.response.LoginResponseDTO;
import org.autoflex.application.services.AuthServiceImplTest;
import org.autoflex.common.exceptions.UnauthorizedException;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class AuthResourceTest {

  @InjectMock AuthServiceImplTest authServiceImpl;

  @Test
  void login_shouldReturn200_whenCredentialsAreValid() {
    LoginRequestDTO request = new LoginRequestDTO("user@autoflex.org", "1234");
    LoginResponseDTO response = new LoginResponseDTO("generated-jwt-token", 7200);

    when(authServiceImpl.authenticate(any(LoginRequestDTO.class))).thenReturn(response);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(200)
        .body("accessToken", is("generated-jwt-token"))
        .body("expires", is(7200))
        .body("tokenType", is("Bearer"));
  }

  @Test
  void login_shouldReturn401_whenCredentialsAreInvalid() {
    LoginRequestDTO request = new LoginRequestDTO("wrong@email.com", "123456");

    when(authServiceImpl.authenticate(any(LoginRequestDTO.class)))
        .thenThrow(new UnauthorizedException());

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(401);
  }

  @Test
  void login_shouldReturn422_whenEmailIsInvalid() {
    LoginRequestDTO invalidRequest = new LoginRequestDTO("", "123");

    given()
        .contentType(ContentType.JSON)
        .body(invalidRequest)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(422);

    verify(authServiceImpl, never()).authenticate(any(LoginRequestDTO.class));
  }

  @Test
  void login_shouldReturn422_whenPasswordIsMissing() {
    LoginRequestDTO invalidRequest = new LoginRequestDTO("admin@autoflex.org", "");

    given()
        .contentType(ContentType.JSON)
        .body(invalidRequest)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(422)
        .body("errors[0].message", is("Password is required"));

    verify(authServiceImpl, never()).authenticate(any(LoginRequestDTO.class));
  }

  @Test
  void login_shouldReturn401_whenUserIsInactive() {
    LoginRequestDTO request = new LoginRequestDTO("inactive@autoflex.org", "1234");

    when(authServiceImpl.authenticate(any(LoginRequestDTO.class)))
        .thenThrow(new UnauthorizedException("User is inactive"));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(401)
        .body("error", is("User is inactive"));
  }

  @Test
  void login_shouldReturn422_whenEmailIsNull() {
    LoginRequestDTO invalidRequest = new LoginRequestDTO(null, "1234");

    given()
        .contentType(ContentType.JSON)
        .body(invalidRequest)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(422);

    verify(authServiceImpl, never()).authenticate(any(LoginRequestDTO.class));
  }

  @Test
  void login_shouldReturn422_whenPasswordIsNull() {
    LoginRequestDTO invalidRequest = new LoginRequestDTO("admin@autoflex.org", null);

    given()
        .contentType(ContentType.JSON)
        .body(invalidRequest)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(422);

    verify(authServiceImpl, never()).authenticate(any(LoginRequestDTO.class));
  }
}
