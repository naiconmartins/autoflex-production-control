package org.autoflex.adapters.outbound.persistence.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.panache.common.Sort;
import org.autoflex.application.dto.SearchQuery;
import org.junit.jupiter.api.Test;

class JpaSortableTest {

  private final JpaSortable sortable = new JpaSortable() {};

  @Test
  void createSort_shouldUseIdAsc_whenSortByIsNullAndDirectionIsNull() {
    SearchQuery query = new SearchQuery(0, 10, null, null);

    Sort result = sortable.createSort(query);

    assertEquals(1, result.getColumns().size());
    assertEquals("id", result.getColumns().getFirst().getName());
    assertEquals(Sort.Direction.Ascending, result.getColumns().getFirst().getDirection());
  }

  @Test
  void createSort_shouldUseIdDesc_whenSortByIsBlankAndDirectionIsDesc() {
    SearchQuery query = new SearchQuery(0, 10, "   ", "desc");

    Sort result = sortable.createSort(query);

    assertEquals(1, result.getColumns().size());
    assertEquals("id", result.getColumns().getFirst().getName());
    assertEquals(Sort.Direction.Descending, result.getColumns().getFirst().getDirection());
  }

  @Test
  void createSort_shouldUseCustomFieldDesc_whenDirectionIsDescIgnoringCase() {
    SearchQuery query = new SearchQuery(0, 10, "name", "DeSc");

    Sort result = sortable.createSort(query);

    assertEquals(1, result.getColumns().size());
    assertEquals("name", result.getColumns().getFirst().getName());
    assertEquals(Sort.Direction.Descending, result.getColumns().getFirst().getDirection());
  }

  @Test
  void createSort_shouldUseCustomFieldAsc_whenDirectionIsInvalid() {
    SearchQuery query = new SearchQuery(0, 10, "code", "invalid");

    Sort result = sortable.createSort(query);

    assertEquals(1, result.getColumns().size());
    assertEquals("code", result.getColumns().getFirst().getName());
    assertEquals(Sort.Direction.Ascending, result.getColumns().getFirst().getDirection());
  }
}
