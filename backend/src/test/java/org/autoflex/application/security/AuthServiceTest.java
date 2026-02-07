package org.autoflex.application.security;

import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.autoflex.domain.entities.User;
import org.autoflex.web.dto.LoginRequestDTO;
import org.autoflex.web.dto.LoginResponseDTO;
import org.autoflex.web.exceptions.UnauthorizedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
public class AuthServiceTest {

    @Inject
    AuthService authService;

    @InjectMock
    PasswordService passwordService;

    @InjectMock
    TokenService tokenService;

    private User mockedUser;
    private LoginRequestDTO defaultDto;
    private TokenService.TokenData mockedTokenData;

    @BeforeEach
    void setUp() {
        PanacheMock.mock(User.class);
        mockedUser = mock(User.class);
        defaultDto = new LoginRequestDTO("admin@autoflex.org", "123456");
        mockedTokenData = new TokenService.TokenData("mocked-jwt-token", 7200);

        lenient().when(mockedUser.isActive()).thenReturn(true);
        lenient().when(mockedUser.getPasswordHash()).thenReturn("hashed_password");
        lenient().when(passwordService.matches(anyString(), anyString())).thenReturn(true);
    }

    @Test
    void authenticate_shouldReturnToken_whenCredentialsAreCorrect() {
        prepareMockUser(defaultDto.email, mockedUser);
        when(tokenService.issue(mockedUser)).thenReturn(mockedTokenData);

        LoginResponseDTO response = authService.authenticate(defaultDto);

        Assertions.assertEquals("mocked-jwt-token", response.accessToken);
        Assertions.assertEquals(7200, response.expires);
        Assertions.assertEquals("Bearer", response.tokenType);
        verify(tokenService).issue(mockedUser);
    }

    @Test
    void authenticate_shouldThrowUnauthorized_whenUserNotFound() {
        prepareMockUser(defaultDto.email, null);

        Assertions.assertThrows(UnauthorizedException.class, () -> authService.authenticate(defaultDto));
        verifyNoInteractions(passwordService, tokenService);
    }

    @Test
    void authenticate_shouldThrowUnauthorized_whenUserIsInactive() {
        when(mockedUser.isActive()).thenReturn(false);
        prepareMockUser(defaultDto.email, mockedUser);

        Assertions.assertThrows(UnauthorizedException.class, () -> authService.authenticate(defaultDto));
        verifyNoInteractions(tokenService);
    }

    @Test
    void authenticate_shouldThrowUnauthorized_whenPasswordDoesNotMatch() {
        prepareMockUser(defaultDto.email, mockedUser);
        when(passwordService.matches(anyString(), anyString())).thenReturn(false);

        Assertions.assertThrows(UnauthorizedException.class, () -> authService.authenticate(defaultDto));
        verifyNoInteractions(tokenService);
    }

    @Test
    void authenticate_shouldThrowException_whenDtoIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> authService.authenticate(null));
    }

    @Test
    void authenticate_shouldReturnCorrectExpirationTime() {
        prepareMockUser(defaultDto.email, mockedUser);
        TokenService.TokenData customTokenData = new TokenService.TokenData("custom-token", 3600);
        when(tokenService.issue(mockedUser)).thenReturn(customTokenData);

        LoginResponseDTO response = authService.authenticate(defaultDto);

        Assertions.assertEquals(3600, response.expires);
        Assertions.assertEquals("custom-token", response.accessToken);
    }

    private void prepareMockUser(String email, User user) {
        when(User.findByEmail(email)).thenReturn(Optional.ofNullable(user));
    }
}