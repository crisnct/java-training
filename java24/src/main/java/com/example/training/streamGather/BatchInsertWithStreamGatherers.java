package com.example.training.streamGather;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

//@formatter:off
/**
 * BatchInsertWithStreamGatherers
 *
 * Purpose:
 *  - Showcase two real-world uses of Java 24 Stream Gatherers:
 *    1) mapConcurrent(maxConcurrency, mapper): concurrent enrichment on virtual threads while preserving order.
 *    2) windowFixed(batchSize): fixed-size batching for efficient JDBC inserts.
 *
 * What it does:
 *  - Generates sample Order data.
 *  - Enriches each Order concurrently (e.g., calling an external service or CPU-heavy step).
 *  - Batches the enriched results (size=500) and executes JDBC batch inserts into an in-memory H2 table.
 *
 * Why this matters:
 *  - mapConcurrent lets you express orderly concurrency directly in a stream pipeline (great for I/O).
 *  - windowFixed eliminates ad-hoc buffering code for batch-oriented sinks (DBs, bulk REST, Kafka).
 *
 * Run notes:
 *  - Add H2 to the runtime classpath (e.g., Maven: com.h2database:h2:2.3.232).
 *  - URL jdbc:h2:mem:test;DB_CLOSE_DELAY=-1 keeps the DB alive during the JVM session.
 */
//@formatter:on
public class BatchInsertWithStreamGatherers {

  // Simple DTOs
  record Order(long id, long customerId, long cents) {

  }

  record EnrichedOrder(long id, long customerId, long cents, int riskScore, Instant enrichedAt) {

  }

  public static void main(String[] args) throws Exception {
    // Optional explicit driver load (not needed if H2 is on classpath)
    // Class.forName("org.h2.Driver");

    List<Order> orders = generateOrders(10_000);

    try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")) {
      createSchema(conn);

      long inserted;
      try (PreparedStatement ps = conn.prepareStatement(
          "INSERT INTO orders(id, customer_id, cents, risk_score, enriched_at) VALUES (?,?,?,?,?)")) {

        Stream<EnrichedOrder> pipeline = orders.stream()
            .gather(Gatherers.mapConcurrent(64, BatchInsertWithStreamGatherers::enrich)) // concurrent enrichment
            .gather(Gatherers.windowFixed(500))                                // fixed-size batching
            .flatMap(List::stream);                                                      // flatten for JDBC loop

        final int[] cnt = {0};
        pipeline.forEach(o -> {
          try {
            ps.setLong(1, o.id());
            ps.setLong(2, o.customerId());
            ps.setLong(3, o.cents());
            ps.setInt(4, o.riskScore());
            ps.setTimestamp(5, Timestamp.from(o.enrichedAt()));
            ps.addBatch();
            if (++cnt[0] % 500 == 0) {
              ps.executeBatch();
            }
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
        ps.executeBatch(); // flush remainder

        inserted = countRows(conn);
      }

      System.out.println("Inserted rows: " + inserted);
    }
  }

  private static EnrichedOrder enrich(Order o) {
    int risk = riskScore(o);
    return new EnrichedOrder(o.id(), o.customerId(), o.cents(), risk, Instant.now());
  }

  private static int riskScore(Order o) {
    busyWork(250); // simulate small cost; adjust as needed
    long x = (o.id() * 31 + o.customerId() * 17 + o.cents());
    return (int) (Math.abs(x) % 100);
  }

  private static void busyWork(int nanos) {
    long target = System.nanoTime() + nanos;
    while (System.nanoTime() < target) { /* spin */ }
  }

  private static List<Order> generateOrders(int n) {
    List<Order> list = new ArrayList<>(n);
    Random rnd = new Random(42);
    for (int i = 1; i <= n; i++) {
      long customer = 1 + rnd.nextInt(5_000);
      long cents = 100 + rnd.nextInt(50_00); // 1.00 .. 50.00
      list.add(new Order(i, customer, cents));
    }
    return list;
  }

  private static void createSchema(Connection conn) throws SQLException {
    try (Statement st = conn.createStatement()) {
      st.execute("""
              create table orders(
                  id           bigint primary key,
                  customer_id  bigint not null,
                  cents        bigint not null,
                  risk_score   int    not null,
                  enriched_at  timestamp not null
              )
          """);
    }
  }

  private static long countRows(Connection conn) throws SQLException {
    try (Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("select count(*) from orders")) {
      rs.next();
      return rs.getLong(1);
    }
  }
}
