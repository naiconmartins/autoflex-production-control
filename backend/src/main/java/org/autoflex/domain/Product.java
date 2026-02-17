package org.autoflex.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.autoflex.common.exceptions.InvalidDataException;

public class Product {

  private Long id;
  private String code;
  private String name;
  private BigDecimal price;
  private List<ProductRawMaterial> rawMaterials = new ArrayList<>();

  public Product() {}

  public Product(String code, String name, BigDecimal price) {
    this.code = code;
    this.name = name;
    setPrice(price);
  }

  public void addRawMaterial(RawMaterial material, BigDecimal quantity) {
    ProductRawMaterial association = new ProductRawMaterial(this, material, quantity);
    this.rawMaterials.add(association);
  }

  public void updateData(String code, String name, BigDecimal price) {
    this.code = code;
    this.name = name;
    setPrice(price);
  }

  public void clearRawMaterials() {
    this.rawMaterials.clear();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    validatePrice(price);
    this.price = price;
  }

  public List<ProductRawMaterial> getRawMaterials() {
    return rawMaterials;
  }

  public void setRawMaterials(List<ProductRawMaterial> rawMaterials) {
    this.rawMaterials = rawMaterials;
  }

  private static void validatePrice(BigDecimal price) {
    if (price == null) {
      throw new InvalidDataException("Product price is required");
    }
    if (price.compareTo(new BigDecimal("0.01")) < 0) {
      throw new InvalidDataException("Price must be greater than zero");
    }
  }
}
