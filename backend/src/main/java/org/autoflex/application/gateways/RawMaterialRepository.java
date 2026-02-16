package org.autoflex.application.gateways;

import java.util.Optional;
import org.autoflex.domain.RawMaterial;

public interface RawMaterialRepository {

  Optional<RawMaterial> findById(Long id);
}
