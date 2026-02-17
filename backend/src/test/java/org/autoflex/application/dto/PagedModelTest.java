package org.autoflex.application.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class PagedModelTest {

  @Test
  void constructor_shouldSetFields_whenCreatedWithValues() {
    PagedModel<String> dto = new PagedModel<>(List.of("A", "B"), 2L, 1);

    assertEquals(2, dto.items().size());
    assertEquals(2L, dto.totalElements());
    assertEquals(1, dto.totalPages());
  }
}
