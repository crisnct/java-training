package com.example.training;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

public class JavaDataTypes {

  // Primitive types (Java 1.0)
  byte b;
  short s;
  int i;
  long l;

  float f;
  double d;

  char c;
  boolean bool;

  // Reference types (Java 1.0)
  String text;
  Object obj;

  // Arrays (existed in Java 1.0)
  int[] numbers;

  // Pre-Collections Framework classes (Java 1.0)
  Vector vector;
  Stack stack;
  Hashtable hashtable;
  StringTokenizer tokenizer;

  public JavaDataTypes() {
    b = 0;
    s = 0;
    i = 0;
    l = 0L;

    f = 0.0f;
    d = 0.0;

    c = '\u0000';
    bool = false;

    text = "";
    obj = new Object();
    numbers = new int[0];

    vector = new Vector();
    vector.add(new Integer(5));
    vector.add("test");
    vector.add("ala bala");
    vector.add(new Byte("3"));
    vector.add(new Double(4.2d));

    System.out.println("Iteration 0---vector---------------------");
    for (int i = 0; i < vector.size(); i++) {
      Object obj = vector.get(i);
      System.out.println(obj);
    }

    System.out.println("Iteration 1----vector--------------------");
    Enumeration elements = vector.elements();
    while (elements.hasMoreElements()) {
      Object obj = elements.nextElement();
      System.out.println(obj);
    }

    System.out.println("Iteration 2-----stack-------------------");
    stack = new Stack();
    stack.add(new Integer(5));
    stack.add("test");
    stack.add("ala bala");
    stack.add(new Byte("3"));
    stack.add(new Double(4.2d));
    stack.push("lastelement");
    while (!stack.isEmpty()){
      Object elem = stack.pop();
      System.out.println(elem);
    }

    System.out.println("Iteration 3-----hashtable-------------------");
    hashtable = new Hashtable(2);
    hashtable.put("1", new Integer(5));
    hashtable.put("2", "test");
    hashtable.put("3", new Byte("3"));
    hashtable.put("4", new Double("4.2"));
    Enumeration elements1 = hashtable.elements();
    while (elements1.hasMoreElements()){
      Object obj = elements1.nextElement();
      System.out.println(obj);
    }

    System.out.println("Iteration 4-----hashtable-------------------");
    tokenizer = new StringTokenizer("test1 test2 test3");
    while (tokenizer.hasMoreTokens()){
      Object word = tokenizer.nextElement();
      System.out.println(word);
    }

  }

  public static void main(String[] args) {
    new JavaDataTypes();
  }

}
