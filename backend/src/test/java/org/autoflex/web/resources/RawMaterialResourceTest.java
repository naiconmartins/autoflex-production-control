package org.autoflex.web.resources;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.autoflex.application.services.RawMaterialService;
import org.autoflex.factory.RawMaterialFactory;
import org.autoflex.web.dto.*;
import org.autoflex.web.exceptions.ConflictException;
import org.autoflex.web.exceptions.DatabaseException;
import org.autoflex.web.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@QuarkusTest
public class RawMaterialResourceTest {

    @InjectMock
    RawMaterialService rawMaterialService;

    @Test
    void insert_shouldReturn401_whenUserIsNotAuthenticated() {
        given()
                .contentType(ContentType.JSON)
                .body(RawMaterialFactory.createRawMaterialRequestDTO())
                .when()
                .post("/raw-material")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn201_whenDataIsValid() {
        RawMaterialRequestDTO request = RawMaterialFactory.createRawMaterialRequestDTO();
        RawMaterialResponseDTO response = RawMaterialFactory.createRawMaterialResponseDTO();

        when(rawMaterialService.insert(any(RawMaterialRequestDTO.class))).thenReturn(response);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/raw-material")
                .then()
                .statusCode(201)
                .header("Location", org.hamcrest.Matchers.containsString("/raw-material/1"))
                .body("code", is("RAW001"));
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn422_whenDataIsInvalid() {
        RawMaterialRequestDTO invalidRequest = new RawMaterialRequestDTO("", null, null);

        given()
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/raw-material")
                .then()
                .statusCode(422);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void insert_shouldReturn409_whenCodeAlreadyExists() {
        when(rawMaterialService.insert(any())).thenThrow(new ConflictException("Code exists"));

        given()
                .contentType(ContentType.JSON)
                .body(RawMaterialFactory.createRawMaterialRequestDTO())
                .when()
                .post("/raw-material")
                .then()
                .statusCode(409);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void update_shouldReturn404_whenIdDoesNotExist() {
        when(rawMaterialService.update(eq(99L), any())).thenThrow(new ResourceNotFoundException("Not found"));

        given()
                .contentType(ContentType.JSON)
                .body(RawMaterialFactory.createRawMaterialRequestDTO())
                .when()
                .put("/raw-material/99")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void update_shouldReturn200_whenDataIsValid() {
        Long id = 1L;
        RawMaterialRequestDTO request = RawMaterialFactory.createRawMaterialRequestDTO();
        RawMaterialResponseDTO response = RawMaterialFactory.createRawMaterialResponseDTO();

        when(rawMaterialService.update(eq(id), any(RawMaterialRequestDTO.class))).thenReturn(response);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/raw-material/{id}", id)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is(response.getName()));
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void update_shouldReturn409_whenCodeAlreadyExists() {
        Long id = 1L;
        RawMaterialRequestDTO request = RawMaterialFactory.createRawMaterialRequestDTO();

        when(rawMaterialService.update(eq(id), any(RawMaterialRequestDTO.class)))
                .thenThrow(new ConflictException("Raw material code already exists"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/raw-material/{id}", id)
                .then()
                .statusCode(409);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void delete_shouldReturn403_whenUserIsNotAdmin() {
        given()
                .when()
                .delete("/raw-material/1")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void delete_shouldReturn204_whenIdExists() {
        given()
                .when()
                .delete("/raw-material/1")
                .then()
                .statusCode(204);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void delete_shouldReturn400_whenDatabaseConflict() {
        doThrow(new DatabaseException("Constraint violation"))
                .when(rawMaterialService).delete(1L);

        given()
                .when()
                .delete("/raw-material/1")
                .then()
                .statusCode(400);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void delete_shouldReturn404_whenIdDoesNotExist() {
        Long invalidId = 99L;

        doThrow(new ResourceNotFoundException("Raw material not found"))
                .when(rawMaterialService).delete(invalidId);

        given()
                .when()
                .delete("/raw-material/{id}", invalidId)
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void findById_shouldReturn200_whenIdExists() {
        RawMaterialResponseDTO response = RawMaterialFactory.createRawMaterialResponseDTO();
        when(rawMaterialService.findById(1L)).thenReturn(response);

        given()
                .when()
                .get("/raw-material/1")
                .then()
                .statusCode(200)
                .body("code", is("RAW001"));
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void findById_shouldReturn404_whenIdDoesNotExist() {
        Long invalidId = 99L;

        when(rawMaterialService.findById(invalidId))
                .thenThrow(new ResourceNotFoundException("Raw material not found"));

        given()
                .when()
                .get("/raw-material/{id}", invalidId)
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void findAll_shouldReturnPageResponse() {
        RawMaterialResponseDTO r1 = RawMaterialFactory.createRawMaterialResponseDTO();
        PageResponseDTO<RawMaterialResponseDTO> pageResponse = new PageResponseDTO<>(
                List.of(r1), 1L, 1, 0, 10
        );

        when(rawMaterialService.findAll(any(PageRequestDTO.class))).thenReturn(pageResponse);

        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/raw-material")
                .then()
                .statusCode(200)
                .body("totalElements", is(1))
                .body("content[0].code", is("RAW001"));
    }

}
