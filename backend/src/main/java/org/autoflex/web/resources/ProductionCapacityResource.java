package org.autoflex.web.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.autoflex.application.services.ProductionCapacityService;
import org.autoflex.web.dto.ProductionPlanResponseDTO;

@Path("/production-capacity")
@Produces(MediaType.APPLICATION_JSON)
public class ProductionCapacityResource {

    @Inject
    ProductionCapacityService productionPlanService;

    @GET
    @RolesAllowed({"ADMIN", "USER"})
    public ProductionPlanResponseDTO generate() {
        return productionPlanService.generate();
    }
}