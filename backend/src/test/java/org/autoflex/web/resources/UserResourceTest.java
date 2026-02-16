package org.autoflex.web.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.autoflex.adapters.inbound.dto.request.UserRequestDTO;
import org.autoflex.adapters.inbound.dto.response.UserResponseDTO;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.common.exceptions.UnauthorizedException;
import org.autoflex.application.services.UserServiceImpl;
import org.autoflex.factory.UserFactory;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UserResourceTest {

  @InjectMock UserServiceImpl userServiceImpl;

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn201_whenDataIsValid() {
    UserRequestDTO request = UserFactory.createUserRequestDTO();
    UserResponseDTO response = UserFactory.createUserResponseDTO(1L, "ADMIN");

    when(userServiceImpl.insert(any(UserRequestDTO.class))).thenReturn(response);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/user")
        .then()
        .statusCode(201)
        .header("Location", org.hamcrest.Matchers.containsString("/user/1"))
        .body("email", is(response.email));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenDataIsBlank() {
    UserRequestDTO request = UserFactory.createCustomUserRequestDTO("", "", "", "", "ADMIN");

    given().contentType(ContentType.JSON).body(request).when().post("/user").then().statusCode(422);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenDataIsNull() {
    UserRequestDTO request = UserFactory.createCustomUserRequestDTO(null, null, null, null, null);

    given().contentType(ContentType.JSON).body(request).when().post("/user").then().statusCode(422);
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void insert_shouldReturn422_whenEmailIsInvalid() {
    UserRequestDTO request =
        UserFactory.createCustomUserRequestDTO("joao.com", "Joao", "Almeida", "123", "ADMIN");

    given().contentType(ContentType.JSON).body(request).when().post("/user").then().statusCode(422);
  }

  @Test
  @TestSecurity(user = "commonUser", roles = "USER")
  void insert_shouldReturn403_whenRoleIsUser() {
    UserRequestDTO request = UserFactory.createUserRequestDTO();

    given().contentType(ContentType.JSON).body(request).when().post("/user").then().statusCode(403);
  }

  @Test
  void insert_shouldReturn401_whenUserIsNotAuthenticated() {
    UserRequestDTO request = UserFactory.createUserRequestDTO();

    given().contentType(ContentType.JSON).body(request).when().post("/user").then().statusCode(401);
  }

  @Test
  void getCurrentUser_shouldReturn401_whenUserIsNotAuthenticated() {
    given().when().get("/user/me").then().statusCode(401);
  }

  @Test
  @TestSecurity(user = "commonUser", roles = "USER")
  void getCurrentUser_shouldReturn200_whenUserRole() {
    UserResponseDTO response = UserFactory.createUserResponseDTO(1L, "USER");
    when(userServiceImpl.getCurrentUser()).thenReturn(response);

    given()
        .when()
        .get("/user/me")
        .then()
        .statusCode(200)
        .body("email", is(response.email))
        .body("active", is(true));
  }

  @Test
  @TestSecurity(user = "admin", roles = "ADMIN")
  void getCurrentUser_shouldReturn200_whenAdminRole() {
    UserResponseDTO response = UserFactory.createUserResponseDTO(1L, "ADMIN");
    when(userServiceImpl.getCurrentUser()).thenReturn(response);

    given()
        .when()
        .get("/user/me")
        .then()
        .statusCode(200)
        .body("email", is(response.email))
        .body("active", is(true));
  }

  @Test
  @TestSecurity(user = "commonUser", roles = "USER")
  void getCurrentUser_shouldReturn404_whenServiceThrowsNotFound() {
    when(userServiceImpl.getCurrentUser())
        .thenThrow(new ResourceNotFoundException("User not found"));

    given().when().get("/user/me").then().statusCode(404);
  }

  @Test
  @TestSecurity(user = "commonUser", roles = "USER")
  void getCurrentUser_shouldReturn401_whenServiceThrowsUnauthorized() {
    when(userServiceImpl.getCurrentUser())
        .thenThrow(new UnauthorizedException("User is not active"));

    given().when().get("/user/me").then().statusCode(401);
  }
}
