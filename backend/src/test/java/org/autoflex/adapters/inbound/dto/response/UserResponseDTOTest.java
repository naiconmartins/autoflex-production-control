package org.autoflex.adapters.inbound.dto.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.autoflex.domain.User;
import org.autoflex.fixtures.DtoFixture;
import org.junit.jupiter.api.Test;

class UserResponseDTOTest {

  @Test
  void constructor_shouldMapFields_whenCreatedFromDomain() {
    User user = DtoFixture.createValidUserDomain();

    UserResponseDTO dto = new UserResponseDTO(user);

    assertEquals(user.getId(), dto.id);
    assertEquals(user.getEmail(), dto.email);
    assertTrue(dto.roles.contains("ADMIN"));
    assertTrue(dto.roles.contains("USER"));
    assertEquals(user.isActive(), dto.active);
    assertEquals(user.getCreatedAt(), dto.createdAt);
  }
}
