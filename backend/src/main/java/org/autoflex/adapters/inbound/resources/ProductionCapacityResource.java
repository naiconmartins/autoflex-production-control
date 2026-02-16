package org.autoflex.adapters.inbound.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.autoflex.adapters.inbound.dto.response.ProductionPlanResponseDTO;
import org.autoflex.application.services.ProductionCapacityImpl;

@Path("/production-capacity")
@Produces(MediaType.APPLICATION_JSON)
public class ProductionCapacityResource {

  @Inject ProductionCapacityImpl productionPlanService;

  @GET
  @RolesAllowed({"ADMIN", "USER"})
  public ProductionPlanResponseDTO generate() {
    return productionPlanService.generate();
  }
}
