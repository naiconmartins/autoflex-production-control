package org.autoflex.adapters.inbound.dto.response;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {
  public List<T> content;
  public long totalElements;
  public int totalPages;
  public int page;
  public int size;

  public static <T> PageResponseDTO<T> of(
      PanacheQuery<?> query, List<T> items, int page, int size) {
    PageResponseDTO<T> r = new PageResponseDTO<>();
    r.content = items;
    r.totalElements = query.count();
    r.totalPages = query.pageCount();
    r.page = page;
    r.size = size;
    return r;
  }
}
