package com.example.training;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.SortedMap;
import java.util.TreeMap;

//@formatter:off
/**
 * Java 21: Sequenced Collections (JEP 431) in practice.
 *
 * What it shows:
 * 1) LruCache using SequencedMap ends operations (putFirst/putLast, firstEntry/lastEntry, pollLastEntry).
 * 2) List as SequencedCollection: addFirst/addLast, getFirst/getLast, removeFirst/removeLast, reversed().
 * 3) LinkedHashSet as SequencedSet: stable order, addFirst/addLast repositions existing elements, reversed() live view.
 */
//@formatter:on
public class SequencedCollectionsDemo {

  public static void main(String[] args) {
    demoLruCache();
    demoListSequencedOps();
    demoLinkedHashSetSequencedOps();
    demoLinkedHashMapAndTreeMap();
    demoUnmodifiableWrappers();
  }

  // -------------------- 1) SequencedMap in a useful LRU-like cache --------------------

  private static void demoLruCache() {
    System.out.println("=== LRU-like cache with SequencedMap ===");
    LruCache cache = new LruCache(3);

    cache.put("A", "alpha"); // A
    cache.put("B", "bravo"); // A B
    cache.put("C", "charlie"); // A B C
    System.out.println(cache.snapshot()); // [A, B, C]

    cache.get("A"); // access A -> move to front (most recent)
    System.out.println(cache.snapshot()); // [A, B, C]

    cache.put("D", "delta"); // capacity=3, evict least recent (last): evicts B
    System.out.println(cache.snapshot()); // [D, A, C]

    cache.get("C"); // touch C -> [C, D, A]
    System.out.println(cache.snapshot()); // [C, D, A]
  }

  private static final class LruCache {

    private final int capacity;
    private final SequencedMap<String, String> map;

    LruCache(int capacity) {
      this.capacity = capacity;
      // LinkedHashMap implements SequencedMap in Java 21
      this.map = new LinkedHashMap<>();
    }

    String get(String key) {
      String val = map.get(key);
      if (val == null) {
        return null;
      }
      // Move to most-recent (front)
      map.putFirst(key, val);
      return val;
    }

    void put(String key, String value) {
      // If key exists, putFirst will both update and move to front
      if (map.containsKey(key)) {
        map.putFirst(key, value);
      } else {
        // Insert as most-recent
        map.putFirst(key, value);
        // Evict least-recent (tail) if over capacity
        if (map.size() > capacity) {
          map.pollLastEntry(); // removes and returns the last (LRU) entry
        }
      }
    }

    List<String> snapshot() {
      // Live reversed views exist, but for printing a stable snapshot is clearer
      List<String> keys = new ArrayList<>(map.sequencedKeySet());
      return keys;
    }
  }

  // -------------------- 2) List as SequencedCollection --------------------

  private static void demoListSequencedOps() {
    System.out.println("\n=== List as SequencedCollection ===");
    SequencedCollection<String> names = new ArrayList<>();

    names.addFirst("Alice");
    names.addLast("Bob");
    names.addLast("Cara");
    System.out.println(names);                // [Alice, Bob, Cara]
    System.out.println(names.getFirst());     // Alice
    System.out.println(names.getLast());      // Cara

    SequencedCollection<String> rev = names.reversed(); // live reverse view
    System.out.println(rev);                   // [Cara, Bob, Alice]

    names.removeFirst();                       // remove head from original
    System.out.println(names);                 // [Bob, Cara]
    System.out.println(rev);                   // [Cara, Bob] (live view reflects change)
  }

  // -------------------- 3) LinkedHashSet as SequencedSet --------------------

  private static void demoLinkedHashSetSequencedOps() {
    System.out.println("\n=== LinkedHashSet as SequencedSet ===");
    SequencedSet<String> tags = new LinkedHashSet<>();

    tags.addLast("java");
    tags.addLast("jvm");
    tags.addLast("loom");
    System.out.println(tags);                  // [java, jvm, loom]

    // Add existing element to the front: it is repositioned, not duplicated
    tags.addFirst("jvm");
    System.out.println(tags);                  // [jvm, java, loom]

    // Reverse live view (useful for "most recent first" reads)
    SequencedSet<String> rev = tags.reversed();
    System.out.println("Reversed: " + rev);                   // [loom, java, jvm]

    tags.removeLast();
    System.out.println(tags);                  // [jvm, java]
    System.out.println(rev);                   // [java, jvm] (live view)
  }

  private static void demoLinkedHashMapAndTreeMap() {
    System.out.println("\n=== SequencedMap: LinkedHashMap (insertion order) ===");

    SequencedMap<String, Integer> lru = new LinkedHashMap<>();
    lru.putFirst("A", 1);   // most-recent at front
    lru.putLast("B", 2);    // least-recent at end
    lru.putFirst("C", 3);
    System.out.println(keys(lru));          // [C, A, B]

    Map.Entry<String, Integer> first = lru.firstEntry();
    Map.Entry<String, Integer> last = lru.lastEntry();
    System.out.println("first=" + first + ", last=" + last); // C=3, B=2

    lru.putFirst("B", 20);  // update and move B to front
    System.out.println(keys(lru));          // [B, C, A]

    lru.pollLastEntry();    // drop least-recent
    System.out.println(keys(lru));          // [B, C]

    // reversed() is a live reverse view
    SequencedMap<String, Integer> lruRev = lru.reversed();
    System.out.println(keys(lruRev));       // [C, B]

    System.out.println("\n=== SequencedMap: TreeMap (sorted order) ===");
    SortedMap<String, Integer> codes = new TreeMap<>();
    // putFirst/putLast exist but order is defined by sort, not insertion point
    codes.put("US", 1);

    try {
      codes.putFirst("DE", 49);
    } catch (UnsupportedOperationException e) {
      System.out.println(
          """
                Expected exception. This is a real constraint: on sorted collections, the “insert at ends” methods are unsupported.
                In Java 21, SortedMap and SortedSet extend the sequenced interfaces for end access and removal
          """
      );
    }
    try {
      codes.putLast("RO", 40);
    } catch (UnsupportedOperationException e) {
      System.out.println(
          """
                Expected exception. This is a real constraint: on sorted collections, the “insert at ends” methods are unsupported.
                In Java 21, SortedMap and SortedSet extend the sequenced interfaces for end access and removal
          """
      );
    }

    System.out.println(keys(codes));        // [DE, RO, US] (sorted by String)
    System.out.println(codes.firstEntry()); // DE=49
    System.out.println(codes.lastEntry());  // US=1
    SequencedMap<String, Integer> codesRev = codes.reversed();
    System.out.println(keys(codesRev));     // [US, RO, DE]
    codes.pollFirstEntry();                  // removes smallest key
    System.out.println(keys(codes));        // [RO, US]
  }

  // helper: extract keys in order for display
  private static <K, V> List<K> keys(SequencedMap<K, V> m) {
    return new ArrayList<>(m.sequencedKeySet());
  }

  // 4) New unmodifiable wrappers for sequenced collections
  private static void demoUnmodifiableWrappers() {
    System.out.println("\n=== Unmodifiable Sequenced wrappers ===");

    SequencedCollection<String> baseList = new ArrayList<>();
    baseList.addLast("one");
    baseList.addLast("two");
    SequencedCollection<String> unmodC =
        Collections.unmodifiableSequencedCollection(baseList);

    SequencedSet<String> baseSet = new LinkedHashSet<>();
    baseSet.addLast("alpha");
    baseSet.addLast("beta");
    SequencedSet<String> unmodS =
        Collections.unmodifiableSequencedSet(baseSet);

    SequencedMap<String, Integer> baseMap = new LinkedHashMap<>();
    baseMap.putLast("x", 1);
    baseMap.putLast("y", 2);
    SequencedMap<String, Integer> unmodM =
        Collections.unmodifiableSequencedMap(baseMap);

    System.out.println(unmodC);             // [one, two]
    System.out.println(unmodS);             // [alpha, beta]
    System.out.println(unmodM.sequencedKeySet()); // [x, y]

    // Verify immutability (each should throw)
    try {
      unmodC.addFirst("zero");
    } catch (UnsupportedOperationException ignored) {
      System.out.println("unmodC blocked addFirst");
    }
    try {
      unmodS.removeLast();
    } catch (UnsupportedOperationException ignored) {
      System.out.println("unmodS blocked removeLast");
    }
    try {
      unmodM.putFirst("z", 3);
    } catch (UnsupportedOperationException ignored) {
      System.out.println("unmodM blocked putFirst");
    }

    // Mutations to the backing collections reflect in the views, as with other unmodifiable wrappers
    baseList.addLast("three");
    System.out.println(unmodC);             // [one, two, three]
  }
}
