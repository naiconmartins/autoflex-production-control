package org.autoflex.domain;

import java.math.BigDecimal;

public class ProductionCapacity {

  public Long productId;
  public String productCode;
  public String productName;
  public BigDecimal unitPrice;
  public BigDecimal producibleQuantity;
  public BigDecimal totalValue;

  public ProductionCapacity() {}

  public ProductionCapacity(
      Long productId,
      String productCode,
      String productName,
      BigDecimal unitPrice,
      BigDecimal producibleQuantity,
      BigDecimal totalValue) {
    this.productId = productId;
    this.productCode = productCode;
    this.productName = productName;
    this.unitPrice = unitPrice;
    this.producibleQuantity = producibleQuantity;
    this.totalValue = totalValue;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public String getProductCode() {
    return productCode;
  }

  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public BigDecimal getProducibleQuantity() {
    return producibleQuantity;
  }

  public void setProducibleQuantity(BigDecimal producibleQuantity) {
    this.producibleQuantity = producibleQuantity;
  }

  public BigDecimal getTotalValue() {
    return totalValue;
  }

  public void setTotalValue(BigDecimal totalValue) {
    this.totalValue = totalValue;
  }
}
