package org.autoflex.application.gateways;

import java.util.List;
import java.util.Optional;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.application.dto.SearchQuery;
import org.autoflex.domain.RawMaterial;

public interface RawMaterialRepository {

  RawMaterial save(RawMaterial rawMaterial);

  Optional<RawMaterial> findById(Long id);

  Optional<RawMaterial> findByCode(String code);

  void delete(Long id);

  PagedModel<RawMaterial> findAll(SearchQuery query);

  PagedModel<RawMaterial> findByName(String name, SearchQuery query);

  List<RawMaterial> listAllRawMaterials();
}
