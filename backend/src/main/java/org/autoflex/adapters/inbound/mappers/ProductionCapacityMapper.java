package org.autoflex.adapters.inbound.mappers;

import org.autoflex.adapters.inbound.dto.response.ProductionPlanResponseDTO;
import org.autoflex.domain.ProductionCapacity;
import org.autoflex.domain.ProductionPlan;

public interface ProductionCapacityMapper {

  ProductionPlanResponseDTO toDomain(ProductionPlan productionPlan);
}
