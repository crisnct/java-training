package com.example.training;

import java.io.IOException;
import java.sql.SQLException;

public class ExceptionsDemo {

  public void run() throws IOException, SQLException {
    try {
      mightThrow();
    } catch (IOException | SQLException e) { // multi-catch
      System.out.println("Handled and rethrowing: " + e.getClass().getSimpleName());
      throw e; // precise rethrow: still only IOException, SQLException
    }
  }

  private void mightThrow() throws IOException, SQLException {
    if (System.currentTimeMillis() % 2 == 0) {
      throw new IOException("IO problem");
    } else {
      throw new SQLException("DB problem");
    }
  }

  public static void main(String[] args) {
    ExceptionsDemo demo = new ExceptionsDemo();
    try {
      demo.run();
    } catch (IOException e) {
      System.out.println("Caller: caught IOException");
    } catch (SQLException e) {
      System.out.println("Caller: caught SQLException");
    }
  }
}
