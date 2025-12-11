package com.example.training.multifileSource.util;

import com.example.training.multifileSource.model.Sale;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//@formatter:off
/**
 * Minimal CSV reader for the demo (no external libs).
 * Skips header; expects: id,product,qty,price
 */
//@formatter:on
public final class CsvUtils {

  private CsvUtils() {}

  public static List<Sale> readSales(BufferedReader reader) throws IOException {
    List<Sale> result = new ArrayList<>();
    String line = reader.readLine(); // header
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.isEmpty()) continue;
      String[] parts = line.split(",", -1);
      if (parts.length < 4) continue;

      long id = Long.parseLong(parts[0].trim());
      String product = parts[1].trim();
      int qty = Integer.parseInt(parts[2].trim());
      double price = Double.parseDouble(parts[3].trim());

      result.add(new Sale(id, product, qty, price));
    }
    return result;
  }
}
