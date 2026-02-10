package org.autoflex.application.security;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.autoflex.domain.entities.User;
import org.autoflex.domain.enums.UserRole;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

@QuarkusTest
public class TokenServiceTest {

    @Inject
    TokenService tokenService;

    private User user;
    private final String EMAIL = "test@autoflex.org";

    @BeforeEach
    void setUp() {
        user = Mockito.mock(User.class);
        Mockito.when(user.getEmail()).thenReturn(EMAIL);
        Mockito.when(user.getRoles()).thenReturn(Set.of(UserRole.USER, UserRole.ADMIN));
    }

    @Test
    void issue_shouldGenerateValidJwtWithCorrectClaims() throws Exception {
        TokenService.TokenData tokenData = tokenService.issue(user);

        Assertions.assertNotNull(tokenData);
        Assertions.assertNotNull(tokenData.getToken());
        Assertions.assertNotNull(tokenData.getExpiresIn());
        Assertions.assertEquals(7200, tokenData.getExpiresIn());

        String token = tokenData.getToken();

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setSkipSignatureVerification()
                .setRequireSubject()
                .build();

        JwtClaims claims = jwtConsumer.processToClaims(token);

        Assertions.assertEquals("autoflex", claims.getIssuer());
        Assertions.assertEquals(EMAIL, claims.getSubject());

        Assertions.assertTrue(claims.getStringListClaimValue("groups").contains("USER"));
        Assertions.assertTrue(claims.getStringListClaimValue("groups").contains("ADMIN"));
    }

    @Test
    void issue_shouldReturnCorrectExpirationTime() {
        TokenService.TokenData tokenData = tokenService.issue(user);

        Assertions.assertEquals(7200, tokenData.getExpiresIn());
    }

    @Test
    void issue_shouldGenerateTokenForDifferentUsers() throws Exception {
        User anotherUser = Mockito.mock(User.class);
        Mockito.when(anotherUser.getEmail()).thenReturn("another@autoflex.org");
        Mockito.when(anotherUser.getRoles()).thenReturn(Set.of(UserRole.USER));

        TokenService.TokenData tokenData1 = tokenService.issue(user);
        TokenService.TokenData tokenData2 = tokenService.issue(anotherUser);

        Assertions.assertNotEquals(tokenData1.getToken(), tokenData2.getToken());

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setSkipSignatureVerification()
                .setRequireSubject()
                .build();

        JwtClaims claims1 = jwtConsumer.processToClaims(tokenData1.getToken());
        JwtClaims claims2 = jwtConsumer.processToClaims(tokenData2.getToken());

        Assertions.assertEquals(EMAIL, claims1.getSubject());
        Assertions.assertEquals("another@autoflex.org", claims2.getSubject());
    }

    @Test
    void issue_shouldIncludeEmptyGroups_whenUserHasNoRoles() throws Exception {
        Mockito.when(user.getRoles()).thenReturn(Set.of());

        TokenService.TokenData tokenData = tokenService.issue(user);

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setSkipSignatureVerification()
                .setRequireSubject()
                .build();

        JwtClaims claims = jwtConsumer.processToClaims(tokenData.getToken());
        Assertions.assertTrue(claims.getStringListClaimValue("groups").isEmpty());
    }

    @Test
    void issue_shouldThrowNullPointerException_whenUserIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> tokenService.issue(null));
    }

    @Test
    void issue_shouldThrowNullPointerException_whenRolesAreNull() {
        Mockito.when(user.getRoles()).thenReturn(null);

        Assertions.assertThrows(NullPointerException.class, () -> tokenService.issue(user));
    }
}
