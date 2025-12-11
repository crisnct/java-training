package com.example.training.multifileSource.report;

import com.example.training.multifileSource.model.Sale;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@formatter:off
/**
 * Aggregates totals by product and prints a small report.
 */
//@formatter:on
public class SalesReport {

  private final Map<String, Totals> byProduct = new HashMap<>();

  public void addAll(List<Sale> sales) {
    for (Sale s : sales) add(s);
  }

  public void add(Sale sale) {
    Totals t = byProduct.get(sale.getProduct());
    if (t == null) {
      t = new Totals();
      byProduct.put(sale.getProduct(), t);
    }
    t.qty += sale.getQuantity();
    t.revenue += sale.total();
  }

  public void printSummary() {
    System.out.println("== Sales summary ==");
    for (Map.Entry<String, Totals> e : byProduct.entrySet()) {
      String product = e.getKey();
      Totals t = e.getValue();
      System.out.printf(" - %s: qty=%d, revenue=%.2f%n", product, t.qty, t.revenue);
    }
  }

  public void printTopProduct() {
    String best = null;
    double max = Double.NEGATIVE_INFINITY;
    for (Map.Entry<String, Totals> e : byProduct.entrySet()) {
      if (e.getValue().revenue > max) {
        max = e.getValue().revenue;
        best = e.getKey();
      }
    }
    System.out.printf("Top product by revenue: %s (%.2f)%n", best, max);
  }

  private static final class Totals {
    int qty;
    double revenue;
  }
}
