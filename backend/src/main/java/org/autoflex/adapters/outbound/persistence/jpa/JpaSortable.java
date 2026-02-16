package org.autoflex.adapters.outbound.persistence.jpa;

import io.quarkus.panache.common.Sort;
import org.autoflex.application.dto.SearchQuery;

public interface JpaSortable {

  default Sort createSort(SearchQuery query) {
    String field = (query.sortBy() == null || query.sortBy().isBlank()) ? "id" : query.sortBy();
    return "desc".equalsIgnoreCase(query.direction())
        ? Sort.by(field).descending()
        : Sort.by(field).ascending();
  }
}
