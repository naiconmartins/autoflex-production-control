package org.autoflex.adapters.inbound.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.autoflex.adapters.inbound.dto.request.PageRequestDTO;
import org.autoflex.application.dto.SearchQuery;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class SearchMapperTest {

  private final SearchMapper mapper = Mappers.getMapper(SearchMapper.class);

  @Test
  void toQuery_shouldMapPageRequestDtoToSearchQuery() {
    PageRequestDTO dto = new PageRequestDTO(1, 20, "name", "desc");

    SearchQuery query = mapper.toQuery(dto);

    assertEquals(dto.page, query.page());
    assertEquals(dto.size, query.size());
    assertEquals(dto.sortBy, query.sortBy());
    assertEquals(dto.direction, query.direction());
  }
}
