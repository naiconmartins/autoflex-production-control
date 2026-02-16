package org.autoflex.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductionPlan {

  public List<ProductionCapacity> items = new ArrayList<>();
  public BigDecimal grandTotalValue = BigDecimal.ZERO;

  public ProductionPlan() {}

  public ProductionPlan(List<ProductionCapacity> items, BigDecimal grandTotalValue) {
    this.items = items;
    this.grandTotalValue = grandTotalValue;
  }

  public List<ProductionCapacity> getItems() {
    return items;
  }

  public void setItems(List<ProductionCapacity> items) {
    this.items = items;
  }

  public BigDecimal getGrandTotalValue() {
    return grandTotalValue;
  }

  public void setGrandTotalValue(BigDecimal grandTotalValue) {
    this.grandTotalValue = grandTotalValue;
  }
}
