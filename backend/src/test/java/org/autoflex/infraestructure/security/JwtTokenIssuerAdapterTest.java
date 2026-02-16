package org.autoflex.infraestructure.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;
import org.autoflex.application.dto.TokenData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JwtTokenIssuerAdapterTest {

  @Test
  void jwtTokenIssue_shouldIssueToken_WhenUnitPath() {
    var adapter = new JwtTokenIssuerAdapter();
    TokenData result = adapter.issue("user@test.com", Set.of("USER"));

    assertNotNull(result);
    assertNotNull(result.token());
  }
}
