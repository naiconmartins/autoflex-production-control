package org.autoflex.domain;

import java.math.BigDecimal;

public class RawMaterial {

  private Long id;
  private String code;
  private String name;
  private BigDecimal stockQuantity;

  public RawMaterial() {}

  public RawMaterial(String code, String name, BigDecimal stockQuantity) {
    this.code = code;
    this.name = name;
    this.stockQuantity = stockQuantity;
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

  public BigDecimal getStockQuantity() {
    return stockQuantity;
  }

  public void setStockQuantity(BigDecimal stockQuantity) {
    this.stockQuantity = stockQuantity;
  }
}
