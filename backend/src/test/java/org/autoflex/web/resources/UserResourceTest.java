package org.autoflex.web.resources;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.autoflex.application.services.UserService;
import org.autoflex.factory.UserFactory;
import org.autoflex.web.dto.UserRequestDTO;
import org.autoflex.web.dto.UserResponseDTO;
import org.junit.jupiter.api.Test;
import io.quarkus.test.security.TestSecurity;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
public class UserResourceTest {

    @InjectMock
    UserService userService;

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn201_whenDataIsValid() {
        UserRequestDTO request = UserFactory.createUserRequestDTO();
        UserResponseDTO response = UserFactory.createUserResponseDTO(1L, "ADMIN");

        when(userService.insert(any(UserRequestDTO.class))).thenReturn(response);

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

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/user")
                .then()
                .statusCode(422);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn422_whenDataIsNull() {
        UserRequestDTO request = UserFactory.createCustomUserRequestDTO(null, null, null, null, null);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/user")
                .then()
                .statusCode(422);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn422_whenEmailIsInvalid() {
        UserRequestDTO request = UserFactory.createCustomUserRequestDTO("joao.com", "Joao", "Almeida", "123", "ADMIN");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/user")
                .then()
                .statusCode(422);
    }

    @Test
    @TestSecurity(user = "commonUser", roles = "USER")
    void insert_shouldReturn403_whenRoleIsUser() {
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
    void insert_shouldReturn401_whenUserIsNotAuthenticated() {
        UserRequestDTO request = UserFactory.createUserRequestDTO();

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/user")
                .then()
                .statusCode(401);
    }
}
