package com.example.training;// Java 17 example: sealed classes and explicit inheritance rules

//@formatter:off
/**
 * final: stop inheritance here
 * sealed...permits: continue restricting subclasses
 * non-sealed: remove the restriction and reopen inheritance completely
 */
//@formatter:on
sealed interface Shape permits Circle, Rectangle, WeirdShape {

  double area();
}

// final: cannot be subclassed
final class Circle implements Shape {

  private final double r;

  Circle(double r) {
    this.r = r;
  }

  @Override
  public double area() {
    return Math.PI * r * r;
  }
}

// sealed: can only be extended by classes listed in permits
sealed class Rectangle implements Shape permits Square, GoldenRectangle {

  protected final double w;
  protected final double h;

  Rectangle(double w, double h) {
    this.w = w;
    this.h = h;
  }

  @Override
  public double area() {
    return w * h;
  }
}

final class Square extends Rectangle {

  Square(double side) {
    super(side, side);
  }
}

final class GoldenRectangle extends Rectangle {

  GoldenRectangle(double w) {
    super(w, w * 1.6180339887);
  }
}

// non-sealed: opens hierarchy again
//From this point downward, any class can extend this type. I don’t want to enforce sealed rules anymore.
non-sealed class WeirdShape implements Shape {

  private double value;

  WeirdShape(double value) {
    this.value = value;
  }

  @Override
  public double area() {
    return value;
  }
}

public class SealedClassesDemo {

  public static void main(String[] args) {
    Shape c = new Circle(5);
    Shape s = new Square(4);
    Shape g = new GoldenRectangle(5);
    Shape w = new WeirdShape(10);

    System.out.println("Circle area = " + c.area());
    System.out.println("Square area = " + s.area());
    System.out.println("Golden rectangle area = " + g.area());
    System.out.println("Weird shape area = " + w.area());
  }
}
