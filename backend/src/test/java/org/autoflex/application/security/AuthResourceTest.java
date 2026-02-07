package org.autoflex.application.security;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.autoflex.web.dto.LoginRequestDTO;
import org.autoflex.web.dto.LoginResponseDTO;
import org.autoflex.web.exceptions.UnauthorizedException;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
public class AuthResourceTest {

    @InjectMock
    AuthService authService;

    @Test
    void login_shouldReturn200_whenCredentialsAreValid() {
        LoginRequestDTO request = new LoginRequestDTO("user@autoflex.org", "1234");
        LoginResponseDTO response = new LoginResponseDTO("generated-jwt-token", 7200);

        when(authService.authenticate(any(LoginRequestDTO.class))).thenReturn(response);

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

        when(authService.authenticate(any(LoginRequestDTO.class)))
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
    }

    @Test
    void login_shouldReturn401_whenUserIsInactive() {
        LoginRequestDTO request = new LoginRequestDTO("inactive@autoflex.org", "1234");

        when(authService.authenticate(any(LoginRequestDTO.class)))
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
}