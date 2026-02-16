package org.autoflex.adapters.outbound.persistence.jpa.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "products",
    uniqueConstraints = {@UniqueConstraint(name = "uk_product_code", columnNames = "code")})
public class JpaProductEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String code;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @OneToMany(
      mappedBy = "product",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<JpaProductRawMaterialEntity> rawMaterials = new ArrayList<>();

  public void addRawMaterial(JpaProductRawMaterialEntity productRawMaterial) {
    rawMaterials.add(productRawMaterial);
    productRawMaterial.setProduct(this);
  }

  public void removeRawMaterial(JpaProductRawMaterialEntity productRawMaterial) {
    rawMaterials.remove(productRawMaterial);
    productRawMaterial.setProduct(null);
  }
}
