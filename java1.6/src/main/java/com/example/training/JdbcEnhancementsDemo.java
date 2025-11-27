package com.example.training;

import java.io.IOException;
import java.sql.*;
import javax.sql.rowset.RowSetProvider;
import javax.sql.rowset.WebRowSet;

public class JdbcEnhancementsDemo {

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        // JDBC 4.0: driver auto-loading (no Class.forName)
        String url = "jdbc:mysql://localhost:3306/testdb";
        String user = "root";
        String pass = "root";

        try {
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Connected successfully.");

            stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS employees (id INT PRIMARY KEY, name VARCHAR(50))");
            stmt.executeUpdate("INSERT INTO employees (id, name) VALUES (1, 'Cris')");

            // --- Enhanced metadata ---
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("Database: " + meta.getDatabaseProductName());
            System.out.println("JDBC version: " +
                    meta.getJDBCMajorVersion() + "." + meta.getJDBCMinorVersion());

            // --- Query and XML integration ---
            rs = stmt.executeQuery("SELECT * FROM employees");
            WebRowSet webRowSet = RowSetProvider.newFactory().createWebRowSet();
            webRowSet.populate(rs);

            System.out.println("\nResult as XML:");
            webRowSet.writeXml(System.out);

        } catch (SQLIntegrityConstraintViolationException e) {
            // new subclass in Java 6
            System.out.println("Duplicate primary key: " + e.getMessage());
        } catch (SQLNonTransientConnectionException e) {
            System.out.println("Connection problem: " + e.getMessage());
        } catch (SQLTransientException e) {
            System.out.println("Temporary DB issue: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        } catch (IOException e) {
          throw new RuntimeException(e);
        } finally {
            // old-school cleanup (no try-with-resources)
            try {
              if (rs != null) {
                rs.close();
              }
            } catch (SQLException ignore) {}
            try {
              if (stmt != null) {
                stmt.close();
              }
            } catch (SQLException ignore) {}
            try {
              if (conn != null) {
                conn.close();
              }
            } catch (SQLException ignore) {}
        }
    }
}
