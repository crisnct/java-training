package com.example.training;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Demonstrație: - JDBC 2.0: batch insert, scrollable result sets (TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY) - Collections (Java 1.2): List și Map
 * pentru cache la nivel de servicii
 * <p>
 * Rulat pe Java 1.3 (fără generics și fără try-with-resources).
 */
public class JDBCDemo {

  // <<< CONFIG >>> — completează cu datele tale
  private static final String JDBC_URL = "jdbc:yourdb://localhost:5432/demo"; // exemplu
  private static final String USER = "user";
  private static final String PASS = "pass";
  private static final String DRIVER_CLASS = "com.your.Driver"; // ex. "org.postgresql.Driver"

  public static void main(String[] args) {
    Connection connection = null;
    try {
      connection = openConnection();

      dropAndCreateSchema(connection);
      insertCustomersBatch(connection);
      ResultSet rs = queryCustomersScrollable(connection);

      Map cacheById = buildCacheFromResultSet(rs);
      rs.close();

      // Exemplu de folosire cache: lookup după id
      Customer c = getCustomerFromCache(cacheById, 2L);
      if (c != null) {
        System.out.println("Found in cache: " + c.id + " -> " + c.fullName + " (" + c.email + ")");
      } else {
        System.out.println("Not found in cache.");
      }

      // Exemple de navigare specifică pe Scrollable ResultSet
      demoScrollOperations(connection);

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      closeQuietly(connection);
    }
  }

  // ---------------- Main tasks ----------------

  private static Connection openConnection() throws Exception {
    Class.forName(DRIVER_CLASS);
    Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASS);
    connection.setAutoCommit(false); // important pentru batch
    return connection;
  }

  private static void dropAndCreateSchema(Connection connection) {
    Statement st = null;
    try {
      st = connection.createStatement();
      // În practică adaptezi pentru dialectul tău
      safeExec(st, "DROP TABLE customers");
      st.executeUpdate("CREATE TABLE customers (" +
          "id BIGINT PRIMARY KEY, " +
          "full_name VARCHAR(200), " +
          "email VARCHAR(200))");
      connection.commit();
    } catch (SQLException e) {
      rollbackQuietly(connection);
      throw new RuntimeException("Schema create failed");
    } finally {
      closeQuietly(st);
    }
  }

  private static void insertCustomersBatch(Connection connection) {
    PreparedStatement ps = null;
    try {
      ps = connection.prepareStatement("INSERT INTO customers (id, full_name, email) VALUES (?, ?, ?)");

      long id = 1L;
      addCustomerBatch(ps, id++, "Ana Pop", "ana.pop@example.com");
      addCustomerBatch(ps, id++, "Mihai Ionescu", "mihai.ionescu@example.com");
      addCustomerBatch(ps, id++, "Ioana Dobre", "ioana.dobre@example.com");
      addCustomerBatch(ps, id++, "Radu Marin", "radu.marin@example.com");

      int[] results = ps.executeBatch();
      // Pentru demo, verificăm că toate au fost executate
      assertBatchAllOk(results);

      connection.commit();
    } catch (SQLException e) {
      rollbackQuietly(connection);
      throw new RuntimeException("Batch insert failed");
    } finally {
      closeQuietly(ps);
    }
  }

  private static ResultSet queryCustomersScrollable(Connection connection) {
    Statement st = null;
    try {
      st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      ResultSet rs = st.executeQuery("SELECT id, full_name, email FROM customers ORDER BY id ASC");
      // lăsăm Statement-ul deschis până terminăm cu ResultSet-ul; îl închidem în buildCacheFromResultSet
      return rs;
    } catch (SQLException e) {
      closeQuietly(st);
      throw new RuntimeException("Scrollable query failed");
    }
  }

  private static Map buildCacheFromResultSet(ResultSet rs) {
    Map cacheById = new HashMap(); // key: Long, value: Customer
    Statement ownerStatement = null;
    try {
      while (rs.next()) {
        long id = rs.getLong("id");
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        cacheById.put(new Long(id), new Customer(id, fullName, email));
      }
      ownerStatement = rs.getStatement();
      return cacheById;
    } catch (SQLException e) {
      throw new RuntimeException("Building cache failed");
    } finally {
      // Închidem Statement-ul asociat RS-ului
      closeQuietly(ownerStatement);
    }
  }

  /**
   * Arată operații specifice Scrollable ResultSet: absolute, relative, last, previous.
   */
  private static void demoScrollOperations(Connection connection) {
    Statement st = null;
    ResultSet rs = null;
    try {
      st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      rs = st.executeQuery("SELECT id, full_name, email FROM customers ORDER BY id ASC");

      if (rs.last()) {
        printRow("LAST", rs);
      }
      if (rs.first()) {
        printRow("FIRST", rs);
      }
      if (rs.absolute(3)) {
        printRow("ABSOLUTE(3)", rs);
      }
      if (rs.relative(-1)) {
        printRow("RELATIVE(-1)", rs);
      }
      // parcurgere inversă completă (exemplu)
      List reversed = new ArrayList();
      rs.afterLast();
      while (rs.previous()) {
        reversed.add(new Long(rs.getLong("id")));
      }
      System.out.println("IDs in reverse: " + reversed);

    } catch (SQLException e) {
      throw new RuntimeException("Scroll demo failed");
    } finally {
      closeQuietly(rs);
      closeQuietly(st);
    }
  }

  // ---------------- Helpers ----------------

  private static void addCustomerBatch(PreparedStatement ps, long id, String fullName, String email) throws SQLException {
    ps.setLong(1, id);
    ps.setString(2, fullName);
    ps.setString(3, email);
    ps.addBatch();
  }

  private static void assertBatchAllOk(int[] results) {
    final int SUCCESS_NO_INFO = -2; // JDBC 2.0
    for (int i = 0; i < results.length; i++) {
      int r = results[i];
      boolean ok = (r >= 0) || (r == SUCCESS_NO_INFO);
      if (!ok) {
        throw new IllegalStateException("One batch item failed at index " + i + " (code=" + r + ")");
      }
    }
  }


  private static Customer getCustomerFromCache(Map cacheById, long id) {
    return (Customer) cacheById.get(new Long(id));
  }

  private static void printRow(String label, ResultSet rs) throws SQLException {
    long id = rs.getLong("id");
    String fullName = rs.getString("full_name");
    String email = rs.getString("email");
    System.out.println(label + " -> id=" + id + ", name=" + fullName + ", email=" + email);
  }

  private static void safeExec(Statement st, String sql) {
    try {
      st.executeUpdate(sql);
    } catch (SQLException ignore) {
      // de ex. DROP pe tabel inexistent
    }
  }

  private static void rollbackQuietly(Connection c) {
    if (c == null) {
      return;
    }
    try {
      c.rollback();
    } catch (Exception ignore) {
    }
  }

  private static void closeQuietly(ResultSet rs) {
    if (rs == null) {
      return;
    }
    try {
      rs.close();
    } catch (Exception ignore) {
    }
  }

  private static void closeQuietly(Statement st) {
    if (st == null) {
      return;
    }
    try {
      st.close();
    } catch (Exception ignore) {
    }
  }

  private static void closeQuietly(Connection c) {
    if (c == null) {
      return;
    }
    try {
      c.close();
    } catch (Exception ignore) {
    }
  }

  // ---------------- Simple DTO ----------------

  private static final class Customer {

    final long id;
    final String fullName;
    final String email;

    Customer(long id, String fullName, String email) {
      this.id = id;
      this.fullName = fullName;
      this.email = email;
    }
  }
}
