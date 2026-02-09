package org.autoflex.domain.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_product_code", columnNames = "code")
        }
)
public class Product extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product code is required")
    @Column(nullable = false, length = 50)
    private String code;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductRawMaterial> rawMaterials = new ArrayList<>();

    public Product() {
    }

    public Product(String code, String name, BigDecimal price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public void addRawMaterial(ProductRawMaterial productRawMaterial) {
        rawMaterials.add(productRawMaterial);
        productRawMaterial.setProduct(this);
    }

    public void removeRawMaterial(ProductRawMaterial productRawMaterial) {
        rawMaterials.remove(productRawMaterial);
        productRawMaterial.setProduct(null);
    }
}
