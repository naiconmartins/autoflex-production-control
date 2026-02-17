package org.autoflex.adapters.inbound.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.autoflex.fixtures.DtoFixture;
import org.junit.jupiter.api.Test;

class PageRequestDTOTest {

  @Test
  void constructor_shouldSetFields_whenAllArgsAreProvided() {
    PageRequestDTO dto = DtoFixture.createValidPageRequestDTO();
    assertEquals(0, dto.page);
    assertEquals(10, dto.size);
    assertEquals("name", dto.sortBy);
    assertEquals("asc", dto.direction);
  }
}
