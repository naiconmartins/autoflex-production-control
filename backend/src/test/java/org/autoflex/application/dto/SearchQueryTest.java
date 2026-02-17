package org.autoflex.application.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SearchQueryTest {

  @Test
  void constructor_shouldSetFields_whenCreatedWithValues() {
    SearchQuery dto = new SearchQuery(1, 20, "name", "desc");

    assertEquals(1, dto.page());
    assertEquals(20, dto.size());
    assertEquals("name", dto.sortBy());
    assertEquals("desc", dto.direction());
  }
}
