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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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
                .post("/raw-materials")
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
                .post("/raw-materials")
                .then()
                .statusCode(201)
                .header("Location", org.hamcrest.Matchers.containsString("/raw-materials/1"))
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
                .post("/raw-materials")
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
                .post("/raw-materials")
                .then()
                .statusCode(409);
    }

    @Test
    void update_shouldReturn401_whenUserIsNotAuthenticated() {
        given()
                .contentType(ContentType.JSON)
                .body(RawMaterialFactory.createRawMaterialRequestDTO())
                .when()
                .put("/raw-materials/{id}", 1L)
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void update_shouldReturn404_whenIdDoesNotExist() {
        when(rawMaterialService.update(eq(99L), any())).thenThrow(new ResourceNotFoundException("Not found"));

        given()
                .contentType(ContentType.JSON)
                .body(RawMaterialFactory.createRawMaterialRequestDTO())
                .when()
                .put("/raw-materials/99")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void update_shouldReturn422_whenDataIsInvalid() {
        RawMaterialRequestDTO invalidRequest = new RawMaterialRequestDTO("", "", null);

        given()
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .put("/raw-materials/{id}", 1L)
                .then()
                .statusCode(422);
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
                .put("/raw-materials/{id}", id)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is(response.getName()));

        verify(rawMaterialService).update(eq(id), any(RawMaterialRequestDTO.class));
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
                .put("/raw-materials/{id}", id)
                .then()
                .statusCode(409);
    }

    @Test
    void delete_shouldReturn401_whenUserIsNotAuthenticated() {
        given()
                .when()
                .delete("/raw-materials/{id}", 1L)
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void delete_shouldReturn403_whenUserIsNotAdmin() {
        given()
                .when()
                .delete("/raw-materials/1")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void delete_shouldReturn204_whenIdExists() {
        given()
                .when()
                .delete("/raw-materials/1")
                .then()
                .statusCode(204);

        verify(rawMaterialService).delete(1L);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    void delete_shouldReturn400_whenDatabaseConflict() {
        doThrow(new DatabaseException("Constraint violation"))
                .when(rawMaterialService).delete(1L);

        given()
                .when()
                .delete("/raw-materials/1")
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
                .delete("/raw-materials/{id}", invalidId)
                .then()
                .statusCode(404);
    }

    @Test
    void findById_shouldReturn401_whenUserIsNotAuthenticated() {
        given()
                .when()
                .get("/raw-materials/{id}", 1L)
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void findById_shouldReturn200_whenIdExists() {
        RawMaterialResponseDTO response = RawMaterialFactory.createRawMaterialResponseDTO();
        when(rawMaterialService.findById(1L)).thenReturn(response);

        given()
                .when()
                .get("/raw-materials/1")
                .then()
                .statusCode(200)
                .body("code", is("RAW001"));

        verify(rawMaterialService).findById(1L);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void findById_shouldReturn404_whenIdDoesNotExist() {
        Long invalidId = 99L;

        when(rawMaterialService.findById(invalidId))
                .thenThrow(new ResourceNotFoundException("Raw material not found"));

        given()
                .when()
                .get("/raw-materials/{id}", invalidId)
                .then()
                .statusCode(404);
    }

    @Test
    void findAll_shouldReturn401_whenUserIsNotAuthenticated() {
        given()
                .when()
                .get("/raw-materials")
                .then()
                .statusCode(401);
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
                .get("/raw-materials")
                .then()
                .statusCode(200)
                .body("totalElements", is(1))
                .body("content[0].code", is("RAW001"));
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void findAll_shouldBindQueryParams_whenProvided() {
        RawMaterialResponseDTO r1 = RawMaterialFactory.createRawMaterialResponseDTO();
        PageResponseDTO<RawMaterialResponseDTO> pageResponse = new PageResponseDTO<>(
                List.of(r1), 1L, 1, 1, 5
        );

        when(rawMaterialService.findAll(any(PageRequestDTO.class))).thenReturn(pageResponse);

        given()
                .queryParam("page", 1)
                .queryParam("size", 5)
                .queryParam("sort", "code")
                .queryParam("dir", "desc")
                .when()
                .get("/raw-materials")
                .then()
                .statusCode(200)
                .body("page", is(1))
                .body("size", is(5));

        verify(rawMaterialService).findAll(argThat(dto ->
                dto.page == 1 && dto.size == 5 && "code".equals(dto.sortBy) && "desc".equals(dto.direction)
        ));
    }

    @Test
    void findByName_shouldReturn401_whenUserIsNotAuthenticated() {
        given()
                .queryParam("name", "steel")
                .when()
                .get("/raw-materials/search")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void findByName_shouldReturn200AndResults_whenNameProvided() {
        RawMaterialResponseDTO r1 = RawMaterialFactory.createRawMaterialResponseDTO();
        PageResponseDTO<RawMaterialResponseDTO> pageResponse = new PageResponseDTO<>(
                List.of(r1), 1L, 1, 1, 5
        );
        when(rawMaterialService.findByName(eq("steel"), any(PageRequestDTO.class))).thenReturn(pageResponse);

        given()
                .queryParam("name", "steel")
                .queryParam("page", 1)
                .queryParam("size", 5)
                .queryParam("sort", "code")
                .queryParam("dir", "desc")
                .when()
                .get("/raw-materials/search")
                .then()
                .statusCode(200)
                .body("totalElements", is(1))
                .body("content[0].code", is("RAW001"));

        verify(rawMaterialService).findByName(eq("steel"), argThat(dto ->
                dto.page == 1 && dto.size == 5 && "code".equals(dto.sortBy) && "desc".equals(dto.direction)
        ));
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    void findByName_shouldReturn200AndEmptyList_whenNameIsBlank() {
        PageResponseDTO<RawMaterialResponseDTO> emptyPage = new PageResponseDTO<>(
                List.of(), 0L, 0, 0, 10
        );
        when(rawMaterialService.findByName(eq("   "), any(PageRequestDTO.class))).thenReturn(emptyPage);

        given()
                .queryParam("name", "   ")
                .when()
                .get("/raw-materials/search")
                .then()
                .statusCode(200)
                .body("content.size()", is(0))
                .body("totalElements", is(0));

        verify(rawMaterialService).findByName(eq("   "), any(PageRequestDTO.class));
    }

}
