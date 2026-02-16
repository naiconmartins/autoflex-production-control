package org.autoflex.adapters.inbound.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.autoflex.adapters.inbound.dto.response.ProductionPlanResponseDTO;
import org.autoflex.adapters.inbound.mappers.ProductionCapacityMapper;
import org.autoflex.application.usecases.ProductionCapacityUseCase;
import org.autoflex.domain.ProductionPlan;

@Path("/production-capacity")
@Produces(MediaType.APPLICATION_JSON)
public class ProductionCapacityResource {

  @Inject ProductionCapacityUseCase productionCapacityUseCase;

  @Inject ProductionCapacityMapper mapper;

  @GET
  @RolesAllowed({"ADMIN", "USER"})
  public Response generate() {
    ProductionPlan productionPlan = productionCapacityUseCase.generate();
    ProductionPlanResponseDTO dto = mapper.toDomain(productionPlan);
    return Response.ok(dto).build();
  }
}
