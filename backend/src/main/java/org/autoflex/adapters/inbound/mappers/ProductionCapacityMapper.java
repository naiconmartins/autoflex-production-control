package org.autoflex.adapters.inbound.mappers;

import org.autoflex.adapters.inbound.dto.response.ProductionPlanResponseDTO;
import org.autoflex.domain.ProductionPlan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface ProductionCapacityMapper {

  ProductionPlanResponseDTO toDomain(ProductionPlan productionPlan);
}
