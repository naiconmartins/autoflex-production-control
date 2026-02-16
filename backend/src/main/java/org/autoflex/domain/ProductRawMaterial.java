package org.autoflex.domain;

import java.math.BigDecimal;

public class ProductRawMaterial {

  private Long id;
  private Product product;
  private RawMaterial rawMaterial;
  private BigDecimal requiredQuantity;

  public ProductRawMaterial() {}

  public ProductRawMaterial(Product product, RawMaterial rawMaterial, BigDecimal requiredQuantity) {
    this.product = product;
    this.rawMaterial = rawMaterial;
    this.requiredQuantity = requiredQuantity;
  }

  public ProductRawMaterial(
      Long id, Product product, RawMaterial rawMaterial, BigDecimal requiredQuantity) {
    this.id = id;
    this.product = product;
    this.rawMaterial = rawMaterial;
    this.requiredQuantity = requiredQuantity;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public RawMaterial getRawMaterial() {
    return rawMaterial;
  }

  public void setRawMaterial(RawMaterial rawMaterial) {
    this.rawMaterial = rawMaterial;
  }

  public BigDecimal getRequiredQuantity() {
    return requiredQuantity;
  }

  public void setRequiredQuantity(BigDecimal requiredQuantity) {
    this.requiredQuantity = requiredQuantity;
  }
}
