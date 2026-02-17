package org.autoflex.adapters.outbound.persistence.jpa.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "raw_materials",
    uniqueConstraints = {@UniqueConstraint(name = "uk_raw_material_code", columnNames = "code")})
public class JpaRawMaterialEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String code;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(name = "stock_quantity", nullable = false, precision = 10, scale = 2)
  private BigDecimal stockQuantity;
}
