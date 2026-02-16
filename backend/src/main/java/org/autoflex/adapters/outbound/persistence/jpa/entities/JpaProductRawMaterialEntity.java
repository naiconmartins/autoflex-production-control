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
    name = "product_raw_materials",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_product_raw_material",
          columnNames = {"product_id", "raw_material_id"})
    })
public class JpaProductRawMaterialEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  private JpaProductEntity product;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "raw_material_id", nullable = false)
  private JpaRawMaterialEntity rawMaterial;

  @Column(name = "required_quantity", nullable = false, precision = 10, scale = 2)
  private BigDecimal requiredQuantity;
}
