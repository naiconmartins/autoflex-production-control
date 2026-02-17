package org.autoflex.adapters.inbound.dto.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LoginResponseDTOTest {

  @Test
  void constructor_shouldSetTokenTypeBearer_whenCreated() {
    LoginResponseDTO dto = new LoginResponseDTO("token", 3600);
    assertEquals("token", dto.accessToken);
    assertEquals(3600, dto.expires);
    assertEquals("Bearer", dto.tokenType);
  }
}
