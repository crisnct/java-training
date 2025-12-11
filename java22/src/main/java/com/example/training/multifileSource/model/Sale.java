package com.example.training.multifileSource.model;

//@formatter:off
/**
 * Simple domain model for a sale entry.
 */
//@formatter:on
public class Sale {

  private final long id;
  private final String product;
  private final int quantity;
  private final double unitPrice;

  public Sale(long id, String product, int quantity, double unitPrice) {
    this.id = id;
    this.product = product;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
  }

  public long getId() {
    return id;
  }

  public String getProduct() {
    return product;
  }

  public int getQuantity() {
    return quantity;
  }

  public double getUnitPrice() {
    return unitPrice;
  }

  public double total() {
    return quantity * unitPrice;
  }
}
