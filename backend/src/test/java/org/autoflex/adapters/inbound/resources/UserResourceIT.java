package org.autoflex.adapters.inbound.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.UUID;
import org.autoflex.adapters.inbound.dto.request.UserRequestDTO;
import org.autoflex.fixtures.UserFixture;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UserResourceIT {

  private static final String EXISTING_EMAIL = "adm@autoflex.com";

  @Test
  @TestSecurity(user = "admin@test.com", roles = "ADMIN")
  void insert_shouldCreateUser_whenValidRequest() {
    UserRequestDTO dto = createValidRequest(uniqueEmail());

    Response response = given().contentType(ContentType.JSON).body(dto).when().post("/user");

    Integer createdId = response.then().statusCode(201).extract().path("id");

    response
        .then()
        .header("Location", endsWith("/user/" + createdId))
        .body("id", is(createdId))
        .body("email", is(dto.email))
        .body("firstName", is(dto.firstName))
        .body("lastName", is(dto.lastName))
        .body("roles", hasItem("USER"))
        .body("active", is(true));
  }

  @Test
  @TestSecurity(user = "common@test.com", roles = "USER")
  void insert_shouldReturn403_whenUserIsNotAdmin() {
    UserRequestDTO dto = createValidRequest(uniqueEmail());

    given().contentType(ContentType.JSON).body(dto).when().post("/user").then().statusCode(403);
  }

  @Test
  void insert_shouldReturn401_whenUserIsNotAuthenticated() {
    UserRequestDTO dto = createValidRequest(uniqueEmail());

    given().contentType(ContentType.JSON).body(dto).when().post("/user").then().statusCode(401);
  }

  @Test
  @TestSecurity(user = "admin@test.com", roles = "ADMIN")
  void insert_shouldReturn422_whenEmailIsNull() {
    UserRequestDTO dto = createValidRequest(uniqueEmail());
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
  @TestSecurity(user = "admin@test.com", roles = "ADMIN")
  void insert_shouldReturn422_whenEmailIsEmpty() {
    UserRequestDTO dto = createValidRequest(uniqueEmail());
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
  @TestSecurity(user = "admin@test.com", roles = "ADMIN")
  void insert_shouldReturn422_whenEmailIsInvalid() {
    UserRequestDTO dto = createValidRequest(uniqueEmail());
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
  @TestSecurity(user = "admin@test.com", roles = "ADMIN")
  void insert_shouldReturn422_whenPasswordIsNull() {
    UserRequestDTO dto = createValidRequest(uniqueEmail());
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
  @TestSecurity(user = "admin@test.com", roles = "ADMIN")
  void insert_shouldReturn422_whenPasswordIsEmpty() {
    UserRequestDTO dto = createValidRequest(uniqueEmail());
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
  @TestSecurity(user = "admin@test.com", roles = "ADMIN")
  void insert_shouldReturn422_whenFirstNameIsNull() {
    UserRequestDTO dto = createValidRequest(uniqueEmail());
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
  @TestSecurity(user = "admin@test.com", roles = "ADMIN")
  void insert_shouldReturn422_whenFirstNameIsEmpty() {
    UserRequestDTO dto = createValidRequest(uniqueEmail());
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
  @TestSecurity(user = "admin@test.com", roles = "ADMIN")
  void insert_shouldReturn422_whenLastNameIsNull() {
    UserRequestDTO dto = createValidRequest(uniqueEmail());
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
  @TestSecurity(user = "admin@test.com", roles = "ADMIN")
  void insert_shouldReturn422_whenLastNameIsEmpty() {
    UserRequestDTO dto = createValidRequest(uniqueEmail());
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
  @TestSecurity(user = "admin@test.com", roles = "ADMIN")
  void insert_shouldReturn422_whenRoleIsNull() {
    UserRequestDTO dto = createValidRequest(uniqueEmail());
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
  @TestSecurity(user = "admin@test.com", roles = "ADMIN")
  void insert_shouldReturn422_whenRoleIsEmpty() {
    UserRequestDTO dto = createValidRequest(uniqueEmail());
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
  @TestSecurity(user = "admin@test.com", roles = "ADMIN")
  void insert_shouldReturn409_whenEmailAlreadyExists() {
    UserRequestDTO dto = createValidRequest(EXISTING_EMAIL);

    given()
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/user")
        .then()
        .statusCode(409)
        .body("error", is("User with email " + EXISTING_EMAIL + " already exists"));
  }

  @Test
  @TestSecurity(user = "admin@test.com", roles = "ADMIN")
  void insert_shouldReturn422_whenRoleIsInvalid() {
    UserRequestDTO dto = createValidRequest(uniqueEmail());
    dto.role = "MANAGER";

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
  @TestSecurity(user = "adm@autoflex.com", roles = "USER")
  void getCurrentUser_shouldReturnUser_whenAuthenticatedAsUser() {
    given()
        .when()
        .get("/user/me")
        .then()
        .statusCode(200)
        .body("email", is("adm@autoflex.com"))
        .body("roles", hasItem("ADMIN"))
        .body("active", is(true));
  }

  @Test
  @TestSecurity(user = "adm@autoflex.com", roles = "ADMIN")
  void getCurrentUser_shouldReturnUser_whenAuthenticatedAsAdmin() {
    given()
        .when()
        .get("/user/me")
        .then()
        .statusCode(200)
        .body("email", is("adm@autoflex.com"))
        .body("roles", hasItem("ADMIN"))
        .body("active", is(true));
  }

  @Test
  @TestSecurity(user = "not-found-user@autoflex.com", roles = "USER")
  void getCurrentUser_shouldReturn404_whenUserNotFoundOrInactive() {
    given()
        .when()
        .get("/user/me")
        .then()
        .statusCode(404)
        .body("error", is("User not found or inactive"));
  }

  @Test
  void getCurrentUser_shouldReturn401_whenUserIsNotAuthenticated() {
    given().when().get("/user/me").then().statusCode(401);
  }

  @Test
  @TestSecurity(user = "guest@test.com", roles = "GUEST")
  void getCurrentUser_shouldReturn403_whenUserHasNoAllowedRole() {
    given().when().get("/user/me").then().statusCode(403);
  }

  private static UserRequestDTO createValidRequest(String email) {
    UserRequestDTO dto = UserFixture.createValidUserRequestDTO();
    dto.email = email;
    return dto;
  }

  private static String uniqueEmail() {
    return "it-user-" + UUID.randomUUID().toString().substring(0, 8).toLowerCase() + "@test.com";
  }
}
