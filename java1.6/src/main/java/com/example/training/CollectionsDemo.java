package com.example.training;

import java.util.*;

public class CollectionsDemo {

    public static void main(String[] args) {
        // Example 1: NavigableMap with TreeMap
        NavigableMap<Integer, String> map = new TreeMap<Integer, String>();
        map.put(10, "Ten");
        map.put(20, "Twenty");
        map.put(30, "Thirty");
        map.put(40, "Forty");

        System.out.println("Lower key than 25: " + map.lowerKey(25));   // 20
        System.out.println("Higher key than 25: " + map.higherKey(25)); // 30
        System.out.println("Ceiling key of 20: " + map.ceilingKey(20)); // 20
        System.out.println("Floor key of 25: " + map.floorKey(25));     // 20

        System.out.println("Descending map: " + map.descendingMap());

        // Example 2: LinkedHashMap with removeEldestEntry()
        LRUCache<Integer, String> cache = new LRUCache<Integer, String>(3);
        cache.put(1, "A");
        cache.put(2, "B");
        cache.put(3, "C");
        cache.put(4, "D"); // oldest (1,"A") will be removed

        System.out.println("LRU Cache contents: " + cache);
    }

    // Custom LRU cache based on LinkedHashMap
    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private int maxSize;

        public LRUCache(int maxSize) {
            super(16, 0.75f, true); // access-order mode
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > maxSize;
        }
    }

}
