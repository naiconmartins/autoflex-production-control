package org.autoflex.adapters.inbound.dto.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import java.util.List;
import org.junit.jupiter.api.Test;

class PageResponseDTOTest {

  @Test
  void of_shouldBuildPageResponse_whenQueryAndItemsAreProvided() {
    @SuppressWarnings("unchecked")
    PanacheQuery<Object> query = mock(PanacheQuery.class);
    when(query.count()).thenReturn(12L);
    when(query.pageCount()).thenReturn(2);

    PageResponseDTO<String> dto = PageResponseDTO.of(query, List.of("A", "B"), 0, 10);

    assertEquals(2, dto.content.size());
    assertEquals(12L, dto.totalElements);
    assertEquals(2, dto.totalPages);
    assertEquals(0, dto.page);
    assertEquals(10, dto.size);
  }
}
