package com.example.training;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.WeakHashMap;

public class PreSizedCollectionsDemo {

  public static void main(String[] args) {

    // Pre-sized HashMap for ~100 mappings without resize
    HashMap<String, Integer> map = HashMap.newHashMap(100);
    map.put("apple", 1);
    map.put("banana", 2);

    // Pre-sized LinkedHashMap for predictable iteration order
    LinkedHashMap<String, Double> linkedMap = LinkedHashMap.newLinkedHashMap(50);
    linkedMap.put("USD", 1.0);
    linkedMap.put("EUR", 0.97);

    // Pre-sized WeakHashMap for caching with weakly referenced keys
    WeakHashMap<Object, String> weakMap = WeakHashMap.newWeakHashMap(30);
    Object key = new Object();
    weakMap.put(key, "cached-value");

    // Pre-sized HashSet for ~200 elements
    HashSet<String> set = HashSet.newHashSet(200);
    set.add("alpha");
    set.add("beta");

    // Pre-sized LinkedHashSet for insertion-order iteration
    LinkedHashSet<String> linkedSet = LinkedHashSet.newLinkedHashSet(75);
    linkedSet.add("one");
    linkedSet.add("two");

    System.out.println("map = " + map);
    System.out.println("linkedMap = " + linkedMap);
    System.out.println("weakMap = " + weakMap);
    System.out.println("set = " + set);
    System.out.println("linkedSet = " + linkedSet);
  }
}
