package com.example.training;

import java.util.HashSet;
import java.util.Set;

public class RecordDemo {

  // JEP 395 — permanent since Java 16
  public record Point(int x, int y) {
    public Point(int x) {
      this(x, 0); // default y
    }
  }

  public static void main(String[] args) {
    Point p1 = new Point(10, 20);
    Point p2 = new Point(10, 20);
    Point p3 = new Point(30);

    System.out.println("p1 = " + p1);
    System.out.println("p2 = " + p2);
    System.out.println("p1.x = " + p1.x());
    System.out.println("p1.y = " + p1.y());

    System.out.println("p1 equals p2? " + p1.equals(p2));

    Set<Point> points = new HashSet<>();
    points.add(p1);
    points.add(p2);
    points.add(p3);

    System.out.println("Set content (no duplicates): " + points);
  }
}
