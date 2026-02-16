package org.autoflex.adapters.inbound.mappers;

import org.autoflex.adapters.inbound.dto.request.PageRequestDTO;
import org.autoflex.application.dto.SearchQuery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface SearchMapper {

  SearchQuery toQuery(PageRequestDTO dto);
}
