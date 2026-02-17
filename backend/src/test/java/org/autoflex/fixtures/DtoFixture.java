package org.autoflex.fixtures;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.autoflex.adapters.inbound.dto.request.LoginRequestDTO;
import org.autoflex.adapters.inbound.dto.request.PageRequestDTO;
import org.autoflex.adapters.inbound.dto.request.ProductRawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.request.ProductRequestDTO;
import org.autoflex.adapters.inbound.dto.request.RawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.request.UserRequestDTO;
import org.autoflex.domain.ProductionCapacity;
import org.autoflex.domain.ProductionPlan;
import org.autoflex.domain.User;
import org.autoflex.domain.UserRole;

public class DtoFixture {

  public static LoginRequestDTO createValidLoginRequestDTO() {
    return new LoginRequestDTO("email@test.com", "secret123");
  }

  public static PageRequestDTO createValidPageRequestDTO() {
    return new PageRequestDTO(0, 10, "name", "asc");
  }

  public static ProductRawMaterialRequestDTO createValidProductRawMaterialRequestDTO() {
    return new ProductRawMaterialRequestDTO(1L, new BigDecimal("10.00"));
  }

  public static ProductRequestDTO createValidProductRequestDTO() {
    return new ProductRequestDTO(
        "PROD-DTO-001",
        "DTO Product",
        new BigDecimal("100.00"),
        List.of(createValidProductRawMaterialRequestDTO()));
  }

  public static RawMaterialRequestDTO createValidRawMaterialRequestDTO() {
    return new RawMaterialRequestDTO("RAW-DTO-001", "DTO Raw Material", new BigDecimal("50.00"));
  }

  public static UserRequestDTO createValidUserRequestDTO() {
    return new UserRequestDTO("email@test.com", "secret123", "Amanda", "Ribeiro", "USER");
  }

  public static User createValidUserDomain() {
    User user = new User();
    user.setId(10L);
    user.setEmail("email@test.com");
    user.setPasswordHash("hash");
    user.setFirstName("Amanda");
    user.setLastName("Ribeiro");
    user.setRoles(Set.of(UserRole.ADMIN, UserRole.USER));
    user.setActive(true);
    user.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
    return user;
  }

  public static ProductionPlan createValidProductionPlan() {
    ProductionCapacity item =
        new ProductionCapacity(
            1L,
            "PROD-001",
            "Dining Table",
            new BigDecimal("1200.00"),
            new BigDecimal("2.00"),
            new BigDecimal("2400.00"));
    return new ProductionPlan(List.of(item), new BigDecimal("2400.00"));
  }
}
