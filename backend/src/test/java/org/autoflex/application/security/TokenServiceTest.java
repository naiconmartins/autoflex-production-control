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
        String token = tokenService.issue(user);

        Assertions.assertNotNull(token);

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
}
