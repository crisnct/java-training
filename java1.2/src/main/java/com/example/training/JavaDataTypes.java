package com.example.training;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;

/**
 * @noinspection FieldCanBeLocal
 */
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

  //Java 1.2
  private final List arrayList;
  private final Set hashSet;
  private final Map hashMap;
  private final Map weakHashMap;
  private final SortedSet sortedSet;
  private final SortedMap sortedMap;

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
    while (!stack.isEmpty()) {
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
    while (elements1.hasMoreElements()) {
      Object obj = elements1.nextElement();
      System.out.println(obj);
    }

    System.out.println("Iteration 4-----hashtable-------------------");
    tokenizer = new StringTokenizer("test1 test2 test3");
    while (tokenizer.hasMoreTokens()) {
      Object word = tokenizer.nextElement();
      System.out.println(word);
    }

    arrayList = new ArrayList();
    arrayList.add("Apple");
    arrayList.add("Banana");
    arrayList.add("Apple"); // duplicate allowed

    hashSet = new HashSet();
    //noinspection OverwrittenKey
    hashSet.add("Apple");
    hashSet.add("Banana");
    //noinspection OverwrittenKey
    hashSet.add("Apple");

    hashMap = new HashMap();
    hashMap.put(new Integer(1), "Red");
    hashMap.put(new Integer(2), "Green");
    hashMap.put(new Integer(3), "Blue");

    System.out.println("List contents: " + arrayList);
    System.out.println("Set contents: " + hashSet);
    System.out.println("Map contents: " + hashMap);

    weakHashMap = new WeakHashMap(2, 0.75f);
    Integer firstKey = new Integer(1);
    String firstValue = "Red";
    weakHashMap.put(firstKey, firstValue);
    weakHashMap.put(new Integer(2), "Green");
    weakHashMap.put(new Integer(3), "Blue");
    weakHashMap.put(new Integer(4), "Cyan");
    weakHashMap.put(new Integer(5), "Magenta");
    try {
      Thread.sleep(1000);
      System.gc();
      if (weakHashMap.isEmpty()) {
        System.out.println("\nWeakHashMap is empty");
      } else {
        System.out.println("\nWeakHashMap values:");
        for (Iterator it = weakHashMap.values().iterator(); it.hasNext(); ) {
          Object val = it.next();
          System.out.println(val);
        }
      }
    } catch (InterruptedException e) {
      throw new RuntimeException("");
    }

    // ===== 2. SORTED VARIANTS =====
    sortedSet = new TreeSet(arrayList);
    sortedMap = new TreeMap(hashMap);

    System.out.println("\nSortedSet: " + sortedSet);
    System.out.println("SortedMap: " + sortedMap);

    // ===== 3. COLLECTIONS UTILITY METHODS =====
    Collections.sort(arrayList);
    Collections.reverse(arrayList);
    Collections.shuffle(arrayList);

    System.out.println("\nList after sort + reverse + shuffle: " + arrayList);

    List unmodifiableList = Collections.unmodifiableList(arrayList);
    List synchronizedList = Collections.synchronizedList(arrayList);

    System.out.println("Unmodifiable list (view): " + unmodifiableList);
    System.out.println("Synchronized list (view): " + synchronizedList);

    // ===== 4. ITERATOR replaces Enumeration =====
    System.out.println("\nIterating using Iterator:");
    Iterator iterator = arrayList.iterator();
    while (iterator.hasNext()) {
      Object element = iterator.next();
      System.out.println(" -> " + element);
      if ("Banana".equals(element)) {
        iterator.remove(); // allowed in Iterator, not in Enumeration
      }
    }

    System.out.println("List after iteration/removal: " + arrayList);

  }

  public static void main(String[] args) {
    new JavaDataTypes();
  }
}
