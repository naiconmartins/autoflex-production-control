package org.autoflex.application.services;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.autoflex.application.security.PasswordService;
import org.autoflex.domain.entities.User;
import org.autoflex.factory.UserFactory;
import org.autoflex.web.dto.UserRequestDTO;
import org.autoflex.web.dto.UserResponseDTO;
import org.autoflex.web.exceptions.ConflictException;
import org.autoflex.web.exceptions.InvalidDataException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@QuarkusTest
public class UserServiceTest {

    @Inject
    UserService userService;
    @InjectMock
    PasswordService passwordService;

    private UserRequestDTO requestDto;
    private MockedStatic<User> userMockedStatic;
    private String existingEmail;

    @BeforeEach
    void setup() {
        existingEmail = "joana@test.com";

        requestDto = UserFactory.createUserRequestDTO();

        userMockedStatic = Mockito.mockStatic(User.class);
        userMockedStatic.when(() -> User.findByEmail(requestDto.email)).thenReturn(Optional.empty());

        when(passwordService.hash(requestDto.password)).thenReturn("$2a$10$7RI3TeWZC47XYC2g3x92luLY75IOV9PAWN53nY54eZ/Dfm2XDvJ5S");
    }

    @AfterEach
    void tearDown() {
        if (userMockedStatic != null) {
            userMockedStatic.close();
        }
    }

    @Test
    void insert_shouldCreateUser_whenValidRequest() {
        UserResponseDTO result = userService.insert(requestDto);

        assertNotNull(result);
        assertEquals(requestDto.email, result.email);
        assertEquals(requestDto.firstName, result.firstName);
        assertEquals(requestDto.lastName, result.lastName);
        assertTrue(result.active);
        assertTrue(result.roles.contains(requestDto.role.toUpperCase()));
    }

    @Test
    void insert_shouldThrowConflictException_whenExistingEmail() {

        UserRequestDTO dto = new UserRequestDTO(
                existingEmail,
                requestDto.password,
                requestDto.firstName,
                requestDto.lastName,
                requestDto.role
        );

        User existingUser = new User();
        userMockedStatic.when(() -> User.findByEmail(existingEmail))
                .thenReturn(Optional.of(existingUser));

        assertThrows(ConflictException.class, () -> {
            userService.insert(dto);
        });
    }

    @Test
    void insert_shouldThrowInvalidDataException_whenRoleIsInvalid() {

        UserRequestDTO dto = new UserRequestDTO(
                requestDto.email,
                requestDto.password,
                requestDto.firstName,
                requestDto.lastName,
                "MANAGER"
        );

        assertThrows(InvalidDataException.class, () -> {
            userService.insert(dto);
        });
    }
}