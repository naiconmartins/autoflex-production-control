package org.autoflex.adapters.inbound.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.autoflex.adapters.inbound.dto.request.UserRequestDTO;
import org.autoflex.application.commands.UserCommand;
import org.autoflex.application.usecases.UserUseCase;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.InvalidDataException;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.domain.User;
import org.autoflex.domain.UserRole;
import org.autoflex.fixtures.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestSecurity(
    user = "admin@test.com",
    roles = {"ADMIN"})
public class UserResourceTest {

  @InjectMock UserUseCase userUseCase;

  private UserRequestDTO dto;
  private User user;

  @BeforeEach
  void setUp() {
    dto = UserFixture.createValidUserRequestDTO();
    user = UserFixture.createValidUser();
    user.setId(1L);
    user.setFirstName(dto.firstName);
    user.setLastName(dto.lastName);
    user.setRoles(java.util.Set.of(UserRole.USER));
  }

  @Test
  void insert_shouldCreateUser_whenValidRequest() {
    when(userUseCase.insert(any(UserCommand.class))).thenReturn(user);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/user")
        .then()
        .statusCode(201)
        .header("Location", endsWith("/user/1"))
        .body("id", is(1))
        .body("email", is(dto.email))
        .body("firstName", is(dto.firstName))
        .body("lastName", is(dto.lastName))
        .body("roles", hasItem("USER"))
        .body("active", is(true));
  }

  @Test
  @TestSecurity(
      user = "common@test.com",
      roles = {"USER"})
  void insert_shouldReturn403_whenUserIsNotAdmin() {
    given().contentType(ContentType.JSON).body(dto).when().post("/user").then().statusCode(403);
  }

  @Test
  @TestSecurity(user = "")
  void insert_shouldReturn401_whenUserIsNotAuthenticated() {
    given().contentType(ContentType.JSON).body(dto).when().post("/user").then().statusCode(401);
  }

  @Test
  void insert_shouldReturn422_whenEmailIsNull() {
    dto.email = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/user")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("email"))
        .body(
            "errors[0].message",
            anyOf(is("Email is required"), is("Email address must not be null")));
  }

  @Test
  void insert_shouldReturn422_whenEmailIsEmpty() {
    dto.email = "";

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/user")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("email"))
        .body(
            "errors[0].message",
            anyOf(is("Email is required"), is("Email address must not be null")));
  }

  @Test
  void insert_shouldReturn422_whenPasswordIsNull() {
    dto.password = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/user")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("password"))
        .body(
            "errors[0].message",
            anyOf(is("Password is required"), is("Password must not be null")));
  }

  @Test
  void insert_shouldReturn422_whenPasswordIsEmpty() {
    dto.password = "";

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/user")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("password"))
        .body(
            "errors[0].message",
            anyOf(is("Password is required"), is("Password must not be null")));
  }

  @Test
  void insert_shouldReturn422_whenFirstNameIsNull() {
    dto.firstName = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/user")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("firstName"))
        .body(
            "errors[0].message",
            anyOf(is("First name is required"), is("First name must not be null")));
  }

  @Test
  void insert_shouldReturn422_whenFirstNameIsEmpty() {
    dto.firstName = "";

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/user")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("firstName"))
        .body(
            "errors[0].message",
            anyOf(is("First name is required"), is("First name must not be null")));
  }

  @Test
  void insert_shouldReturn422_whenLastNameIsNull() {
    dto.lastName = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/user")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("lastName"))
        .body(
            "errors[0].message",
            anyOf(is("Last name is required"), is("Last name must not be null")));
  }

  @Test
  void insert_shouldReturn422_whenLastNameIsEmpty() {
    dto.lastName = "";

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/user")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("lastName"))
        .body(
            "errors[0].message",
            anyOf(is("Last name is required"), is("Last name must not be null")));
  }

  @Test
  void insert_shouldReturn422_whenEmailIsInvalid() {
    dto.email = "invalid-email";

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/user")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("email"))
        .body("errors[0].message", is("Invalid email format"));
  }

  @Test
  void insert_shouldReturn422_whenRoleIsNull() {
    dto.role = null;

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/user")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("role"))
        .body("errors[0].message", anyOf(is("Role name is required"), is("Role must not be null")));
  }

  @Test
  void insert_shouldReturn422_whenRoleIsEmpty() {
    dto.role = "";

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/user")
        .then()
        .statusCode(422)
        .body("errors[0].field", is("role"))
        .body("errors[0].message", anyOf(is("Role name is required"), is("Role must not be null")));
  }

  @Test
  void insert_shouldReturn409_whenEmailAlreadyExists() {
    when(userUseCase.insert(any(UserCommand.class)))
        .thenThrow(new ConflictException("User with email email@test.com already exists"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/user")
        .then()
        .statusCode(409)
        .body("error", is("User with email email@test.com already exists"));
  }

  @Test
  void insert_shouldReturn422_whenRoleIsInvalid() {
    dto = UserFixture.createUserRequestWithRole("MANAGER");

    when(userUseCase.insert(any(UserCommand.class)))
        .thenThrow(new InvalidDataException("Invalid role: MANAGER"));

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/user")
        .then()
        .statusCode(422)
        .body("error", is("Invalid role: MANAGER"));
  }

  @Test
  @TestSecurity(
      user = "email@test.com",
      roles = {"USER"})
  void getCurrentUser_shouldReturnUser_whenAuthenticatedAsUser() {
    when(userUseCase.findByEmail("email@test.com")).thenReturn(user);

    given()
        .when()
        .get("/user/me")
        .then()
        .statusCode(200)
        .body("id", is(1))
        .body("email", is("email@test.com"))
        .body("roles", hasItem("USER"))
        .body("active", is(true));

    verify(userUseCase).findByEmail("email@test.com");
  }

  @Test
  @TestSecurity(
      user = "admin@test.com",
      roles = {"ADMIN"})
  void getCurrentUser_shouldReturnUser_whenAuthenticatedAsAdmin() {
    user.setRoles(java.util.Set.of(UserRole.ADMIN));
    when(userUseCase.findByEmail("admin@test.com")).thenReturn(user);

    given().when().get("/user/me").then().statusCode(200).body("email", is("email@test.com"));

    verify(userUseCase).findByEmail("admin@test.com");
  }

  @Test
  @TestSecurity(
      user = "email@test.com",
      roles = {"USER"})
  void getCurrentUser_shouldReturn404_whenUserNotFoundOrInactive() {
    when(userUseCase.findByEmail("email@test.com"))
        .thenThrow(new ResourceNotFoundException("User not found or inactive"));

    given()
        .when()
        .get("/user/me")
        .then()
        .statusCode(404)
        .body("error", is("User not found or inactive"));
  }

  @Test
  @TestSecurity(user = "")
  void getCurrentUser_shouldReturn401_whenUserIsNotAuthenticated() {
    given().when().get("/user/me").then().statusCode(401);
  }

  @Test
  @TestSecurity(
      user = "guest@test.com",
      roles = {"GUEST"})
  void getCurrentUser_shouldReturn403_whenUserHasNoAllowedRole() {
    given().when().get("/user/me").then().statusCode(403);
  }
}
