package org.autoflex.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.security.Principal;
import java.time.Instant;
import java.util.Optional;
import org.autoflex.adapters.inbound.dto.request.UserRequestDTO;
import org.autoflex.adapters.inbound.dto.response.UserResponseDTO;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.InvalidDataException;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.common.exceptions.UnauthorizedException;
import org.autoflex.application.security.PasswordService;
import org.autoflex.domain.User;
import org.autoflex.domain.UserRole;
import org.autoflex.factory.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

@QuarkusTest
public class UserServiceImplTest {

  @Inject UserServiceImpl userServiceImpl;
  @InjectMock PasswordService passwordService;
  @InjectMock SecurityIdentity securityIdentity;

  private UserRequestDTO requestDto;
  private MockedStatic<User> userMockedStatic;
  private String existingEmail;

  @BeforeEach
  void setup() {
    existingEmail = "joana@test.com";

    requestDto = UserFactory.createUserRequestDTO();

    userMockedStatic = Mockito.mockStatic(User.class);
    userMockedStatic.when(() -> User.findByEmail(requestDto.email)).thenReturn(Optional.empty());

    when(passwordService.hash(requestDto.password))
        .thenReturn("$2a$10$7RI3TeWZC47XYC2g3x92luLY75IOV9PAWN53nY54eZ/Dfm2XDvJ5S");
  }

  @AfterEach
  void tearDown() {
    if (userMockedStatic != null) {
      userMockedStatic.close();
    }
  }

  @Test
  void insert_shouldCreateUser_whenValidRequest() {
    UserResponseDTO result = userServiceImpl.insert(requestDto);

    assertNotNull(result);
    assertEquals(requestDto.email, result.email);
    assertEquals(requestDto.firstName, result.firstName);
    assertEquals(requestDto.lastName, result.lastName);
    assertTrue(result.active);
    assertTrue(result.roles.contains(requestDto.role.toUpperCase()));
  }

  @Test
  void insert_shouldThrowConflictException_whenExistingEmail() {

    UserRequestDTO dto =
        new UserRequestDTO(
            existingEmail,
            requestDto.password,
            requestDto.firstName,
            requestDto.lastName,
            requestDto.role);

    User existingUser = new User();
    userMockedStatic
        .when(() -> User.findByEmail(existingEmail))
        .thenReturn(Optional.of(existingUser));

    assertThrows(
        ConflictException.class,
        () -> {
          userServiceImpl.insert(dto);
        });
  }

  @Test
  void insert_shouldThrowInvalidDataException_whenRoleIsInvalid() {

    UserRequestDTO dto =
        new UserRequestDTO(
            requestDto.email,
            requestDto.password,
            requestDto.firstName,
            requestDto.lastName,
            "MANAGER");

    assertThrows(
        InvalidDataException.class,
        () -> {
          userServiceImpl.insert(dto);
        });
  }

  @Test
  void getCurrentUser_shouldThrowUnauthorizedException_whenAnonymous() {
    when(securityIdentity.isAnonymous()).thenReturn(true);

    UnauthorizedException ex =
        assertThrows(UnauthorizedException.class, () -> userServiceImpl.getCurrentUser());
    assertEquals("User is not authenticated", ex.getMessage());
  }

  @Test
  void getCurrentUser_shouldThrowResourceNotFoundException_whenUserDoesNotExist() {
    String email = "missing@autoflex.org";
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn((Principal) () -> email);
    userMockedStatic.when(() -> User.findByEmail(email)).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> userServiceImpl.getCurrentUser());
    assertEquals("User not found", ex.getMessage());
  }

  @Test
  void getCurrentUser_shouldThrowUnauthorizedException_whenUserIsInactive() {
    String email = "inactive@autoflex.org";
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn((Principal) () -> email);

    User user = new User();
    user.setId(1L);
    user.setEmail(email);
    user.setFirstName("Inactive");
    user.setLastName("User");
    user.addRole(UserRole.USER);
    user.setActive(false);
    user.setCreatedAt(Instant.now());

    userMockedStatic.when(() -> User.findByEmail(email)).thenReturn(Optional.of(user));

    UnauthorizedException ex =
        assertThrows(UnauthorizedException.class, () -> userServiceImpl.getCurrentUser());
    assertEquals("User is not active", ex.getMessage());
  }

  @Test
  void getCurrentUser_shouldReturnUserResponse_whenAuthenticatedAndActive() {
    String email = "current@autoflex.org";
    when(securityIdentity.isAnonymous()).thenReturn(false);
    when(securityIdentity.getPrincipal()).thenReturn((Principal) () -> email);

    User user = new User();
    user.setId(10L);
    user.setEmail(email);
    user.setFirstName("Current");
    user.setLastName("User");
    user.addRole(UserRole.USER);
    user.setActive(true);
    user.setCreatedAt(Instant.now());

    userMockedStatic.when(() -> User.findByEmail(email)).thenReturn(Optional.of(user));

    UserResponseDTO result = userServiceImpl.getCurrentUser();

    assertNotNull(result);
    assertEquals(email, result.email);
    assertTrue(result.active);
    assertTrue(result.roles.contains("USER"));
  }
}
