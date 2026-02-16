package org.autoflex.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.quarkus.test.Mock;
import java.util.Optional;
import org.autoflex.application.commands.LoginCommand;
import org.autoflex.application.dto.TokenData;
import org.autoflex.application.gateways.PasswordEncoder;
import org.autoflex.application.gateways.TokenIssuer;
import org.autoflex.application.gateways.UserRepository;
import org.autoflex.common.exceptions.UnauthorizedException;
import org.autoflex.fixtures.UserFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

  @Mock UserRepository userRepository;
  @Mock PasswordEncoder passwordEncoder;
  @Mock TokenIssuer tokenIssuer;

  @InjectMocks AuthServiceImpl authService;

  private LoginCommand cmd;

  void setUp() {
    cmd = new LoginCommand("email@test.com", "1234");
  }

  @Test
  void authenticate_shouldReturnToken_whenCredentialsAreValid() {
    var validUser = UserFixture.createValidUser();
    var expectedToken = new TokenData("jwt-token-valido", 7200);

    when(userRepository.findByEmail(cmd.email())).thenReturn(Optional.of(validUser));
    when(passwordEncoder.matches(cmd.password(), validUser.getPasswordHash())).thenReturn(true);
    when(tokenIssuer.issue(eq(validUser.getEmail()), anySet())).thenReturn(expectedToken);

    TokenData result = authService.authenticate(cmd);

    assertNotNull(result);
    assertEquals(expectedToken.token(), result.token());
    assertEquals(expectedToken.expiresIn(), result.expiresIn());

    verify(userRepository).findByEmail(cmd.email());
    verify(passwordEncoder).matches(anyString(), anyString());
  }

  @Test
  void authenticate_shouldThrowUnauthorized_whenUserNotFound() {
    when(userRepository.findByEmail(cmd.email())).thenReturn(Optional.empty());

    Assertions.assertThrows(UnauthorizedException.class, () -> authService.authenticate(cmd));
    verifyNoInteractions(tokenIssuer);
  }

  @Test
  void authenticate_shouldThrowUnauthorized_whenUserIsInactive() {
    var userInactive = UserFixture.createInactiveUser();
    when(userRepository.findByEmail(cmd.email())).thenReturn(Optional.of(userInactive));

    Assertions.assertThrows(UnauthorizedException.class, () -> authService.authenticate(cmd));
    verifyNoInteractions(tokenIssuer);
  }

  @Test
  void authenticate_shouldThrowUnauthorized_whenPasswordDoesNotMatch() {
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

    Assertions.assertThrows(UnauthorizedException.class, () -> authService.authenticate(cmd));
    verifyNoInteractions(tokenIssuer);
  }

  @Test
  void authenticate_shouldThrowException_whenDtoIsNull() {
    Assertions.assertThrows(NullPointerException.class, () -> authService.authenticate(null));
  }
}
