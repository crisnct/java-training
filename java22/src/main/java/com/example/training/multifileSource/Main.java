package com.example.training.multifileSource;

import com.example.training.multifileSource.model.Sale;
import com.example.training.multifileSource.report.SalesReport;
import com.example.training.multifileSource.util.CsvUtils;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

//@formatter:off
/**
 * Entry point for a multi-file source program (Java 22).
 * Demonstrates launching directly from sources spanning multiple packages.
 *
 * You can run Java code straight from .java files without compiling first, even
 * if your program is split across multiple files. The java launcher compiles them in memory, then runs your app.
 *
 * How to run (no compilation step needed):
 *   java app/Main.java model/Sale.java util/CsvUtils.java report/SalesReport.java
 */
//@formatter:on
public class Main {

  private static final String SAMPLE_CSV = """
      id,product,qty,price
      1,Coffee Beans,3,29.90
      2,Tea,2,19.50
      3,Coffee Beans,1,29.90
      4,Mug,5,7.25
      5,Tea,3,19.50
      """;

  public static void main(String[] args) {
    List<Sale> sales = readSalesFromCsv(SAMPLE_CSV);

    SalesReport report = new SalesReport();
    report.addAll(sales);

    report.printSummary();
    report.printTopProduct();
  }

  private static List<Sale> readSalesFromCsv(String csv) {
    try (BufferedReader reader = new BufferedReader(new StringReader(csv))) {
      return CsvUtils.readSales(reader);
    } catch (Exception e) {
      throw new RuntimeException("Failed to load CSV", e);
    }
  }
}
