package org.autoflex.application.usecases;

import org.autoflex.application.commands.RawMaterialCommand;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.application.dto.SearchQuery;
import org.autoflex.domain.RawMaterial;

public interface RawMaterialUseCase {

  RawMaterial insert(RawMaterialCommand cmd);

  RawMaterial update(Long id, RawMaterialCommand cmd);

  void delete(Long id);

  RawMaterial findById(Long id);

  PagedModel<RawMaterial> findAll(SearchQuery query);

  PagedModel<RawMaterial> findByName(String name, SearchQuery query);
}
