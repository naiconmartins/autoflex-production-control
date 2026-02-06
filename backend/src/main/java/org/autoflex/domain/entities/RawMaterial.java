package org.autoflex.domain.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.autoflex.web.dto.RawMaterialRequestDTO;
import org.autoflex.web.exceptions.InvalidDataException;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Table(
        name = "raw_materials",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_raw_material_code", columnNames = "code")
        }
)
public class RawMaterial extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Raw material code is required")
    @Column(nullable = false, length = 50, updatable = false)
    private String code;

    @NotBlank(message = "Raw material name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "Stock quantity is required")
    @DecimalMin(value = "0.0", message = "Stock quantity cannot be negative")
    @Column(name = "stock_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockQuantity;

    public RawMaterial() {
    }

    public RawMaterial(String code, String name, BigDecimal stockQuantity) {
        this.code = code;
        this.name = name;
        this.stockQuantity = stockQuantity;
    }

    public RawMaterial(RawMaterialRequestDTO dto) {
        this.code = dto.code;
        this.name = dto.name;
        this.stockQuantity = dto.stockQuantity;
    }

    public boolean hasStock(BigDecimal requiredQuantity) {
        return this.stockQuantity.compareTo(requiredQuantity) >= 0;
    }

    public void consumeStock(BigDecimal quantity) {
        if (quantity.signum() < 0) {
            throw new InvalidDataException("Quantity must be positive");
        }
        if (this.stockQuantity.subtract(quantity).signum() < 0) {
            throw new InvalidDataException("Insufficient stock");
        }
        this.stockQuantity = this.stockQuantity.subtract(quantity);
    }
}
