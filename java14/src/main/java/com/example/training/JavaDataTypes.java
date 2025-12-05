package com.example.training;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * @noinspection FieldCanBeLocal, StringOperationCanBeSimplified
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
  Vector<Object> vector;
  Stack stack;
  Hashtable hashtable;
  StringTokenizer tokenizer;

  //Java 1.2
  private ArrayList arrayList;
  private HashSet hashSet;
  private TreeSet treeSet;
  private HashMap hashMap;
  private WeakHashMap weakHashMap;
  private TreeMap treeMap;

  //Java 1.3
  //Introducing StrictMath. Same result on all platforms
  double v = StrictMath.cos(1.2345);

  //Java 1.4
  private LinkedHashMap linkedHashMap;
  private LinkedHashSet linkedHashSet;
  private IdentityHashMap identityHashMap;

  // Java 5 – new collection types
  List oldList = new ArrayList();  // fără generics, backward compatible
  private EnumSet<Level> enumSet;
  private EnumMap<Level, String> enumMap;

  private ConcurrentHashMap<String, Integer> concurrentHashMap;
  private CopyOnWriteArrayList<String> copyOnWriteArrayList;
  private CopyOnWriteArraySet<String> copyOnWriteArraySet;

  private ArrayBlockingQueue<String> arrayBlockingQueue;
  private LinkedBlockingQueue<String> linkedBlockingQueue;
  private PriorityBlockingQueue<Integer> priorityBlockingQueue;

  private DelayQueue<DelayedTask> delayQueue;
  private SynchronousQueue<String> synchronousQueue;
  private ConcurrentLinkedQueue<String> concurrentLinkedQueue;

  //  Java 6
  //  Optimizations at:
  //  - Collections.synchronizedList(), synchronizedMap()
  //  - TreeMap implements NavigableMap: higherKey(), lowerKey(), ceilingKey(), floorKey(), pollFirstEntry(), pollLastEntry(), etc
  //  - TreeSet implements NavigableSet: higherKey(), lowerKey(), ceilingKey(), floorKey(), pollFirstEntry(), pollLastEntry(), etc
  //  - LinkedHashMap has removeEldestEntry method, useful for cache mechanism
  //  - Collections.synchronizedList(), synchronizedMap()
  //  - ConcurrentHashMap
  //  - CopyOnWriteArrayList
  //  - CopyOnWriteArraySet
  //  - PriorityBlockingQueue
  //  - DelayQueue

  //  Java 7
  //diamond operator
  private List<String> names = new ArrayList<>();
  private Map<String, Integer> counts = new HashMap<>();
  private Map<String, List<Integer>> data = new HashMap<>();

  //Good for producer–consumer patterns or lock-free queues.
  private ConcurrentLinkedDeque<String> concurrentLinkedDeque = new ConcurrentLinkedDeque<>();
  //High-throughput queue optimized for hand-off between threads.
  private LinkedTransferQueue<String> linkedTransferQueue = new LinkedTransferQueue<>();

  //  Java 8 - no new collections

  //  Java 9 - no new collections, but new utility classes
  //  Characteristics of List.of, Set.of, Map.of
  //  - Immutable (cannot add/remove/modify)
  //  - Optimized for memory
  //  - Fail-fast on null keys or values
  //  - Much faster than Collections.unmodifiableList(...) wrappers
  private final List<String> names2 = List.of("Alice", "Bob", "Charlie");
  private final Set<Integer> numbers2 = Set.of(1, 2, 3);
  private final Map<String, Integer> ages = Map.of("John", 30, "Ana", 25);
  private final Map<String, Integer> ages2 = Map.ofEntries(
      Map.entry("John", 30),
      Map.entry("Ana", 25),
      Map.entry("Mark", 40)
  );

  //  Java 10 - no new collections. Appear new methods:
  //  List.copyOf(existingList), Set.copyOf(existingSet), Map.copyOf(existingMap)

  //  Java 11 - no new collections, performance tweaks. Appear:
  // Collection.toArray(IntFunction<T[]>)

  //  Java 12 - no new collections

  public JavaDataTypes() throws InterruptedException {
    Primitives();
    Vector();
    Hashtable();
    StringTokenizer();

    WeakHashMap();
    TreeMap();
    LinkedHashMap();
    EnumMap();
    ConcurrentHashMap();
    IdentityHashMap();

    ArrayList();
    CopyOnWriteArrayList();

    HashSet();
    LinkedHashSet();
    CopyOnWriteArraySet();
    EnumSet();
    TreeSet();

    Stack();
    ConcurrentLinkedDeque();

    ArrayBlockingQueue();
    LinkedBlockingQueue();
    PriorityBlockingQueue();

    DelayQueue();
    SynchronousQueue();
    ConcurrentLinkedQueue();
    LinkedTransferQueue();

  }

  private void Primitives() {
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
  }

  private void Vector() {
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
  }

  private void Stack() {
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
  }

  private void Hashtable() {
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
  }

  private void StringTokenizer() {
    System.out.println("Iteration 4-----hashtable-------------------");
    tokenizer = new StringTokenizer("test1 test2 test3");
    while (tokenizer.hasMoreTokens()) {
      Object word = tokenizer.nextElement();
      System.out.println(word);
    }
  }

  private void HashSet() {
    hashSet = new HashSet();
    //noinspection OverwrittenKey
    hashSet.add("Apple");
    hashSet.add("Banana");
    //noinspection OverwrittenKey
    hashSet.add("Apple");
    System.out.println("Set contents: " + hashSet);
  }

  private void WeakHashMap() {
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
  }

  private void TreeMap() {
    hashMap = new HashMap();
    hashMap.put(new Integer(1), "Red");
    hashMap.put(new Integer(2), "Green");
    hashMap.put(new Integer(3), "Blue");

    treeMap = new TreeMap(hashMap);
    System.out.println("SortedMap: " + treeMap);
  }

  private void TreeSet() {
    arrayList = new ArrayList();
    arrayList.add("Apple");
    arrayList.add("Banana");
    arrayList.add("Apple"); // duplicate allowed

    treeSet = new TreeSet(arrayList);
    System.out.println("\nSortedSet: " + treeSet);
  }

  private void ArrayList() {
    arrayList = new ArrayList();
    arrayList.add("Apple");
    arrayList.add("Banana");
    arrayList.add("Apple"); // duplicate allowed

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

  private void LinkedHashMap() {
    System.out.println("=== LinkedHashMap (insertion order) ===");
    this.linkedHashMap = new LinkedHashMap();
    linkedHashMap.put("A", "alpha");
    linkedHashMap.put("C", "charlie");
    linkedHashMap.put("B", "bravo");
    for (Iterator it = linkedHashMap.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry e = (Map.Entry) it.next();
      System.out.println(e.getKey() + " -> " + e.getValue());
    }
  }

  private void IdentityHashMap() {
    System.out.println("\n=== IdentityHashMap (key by identity) ===");
    this.identityHashMap = new IdentityHashMap(); // compar�� cheile cu '=='
    // Dou�� String-uri cu acelaETi conE>inut, dar instanE>e distincte
    String k1 = new String("key");
    String k2 = new String("key"); // alt obiect

    identityHashMap.put(k1, "V1");
    identityHashMap.put(k2, "V2");

    // DeETi k1.equals(k2) este true, sunt obiecte diferite => 2 intr��ri
    for (Iterator it = identityHashMap.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry e = (Map.Entry) it.next();
      System.out.println(e.getKey() + " (id=" + System.identityHashCode(e.getKey()) + ") -> " + e.getValue());
    }
  }

  private void LinkedHashSet() {
    System.out.println("\n=== LinkedHashSet (insertion order) ===");
    this.linkedHashSet = new LinkedHashSet();
    linkedHashSet.add("one");
    linkedHashSet.add("three");
    linkedHashSet.add("two");
    for (Iterator it = linkedHashSet.iterator(); it.hasNext(); ) {
      Object v = it.next();
      System.out.println(v);
    }
  }

  private void EnumSet() {
    // EnumSet – set ultra-rapid pentru enum-uri
    enumSet = EnumSet.of(Level.LOW, Level.MEDIUM);
    enumSet.add(Level.HIGH);
    System.out.println("EnumSet contents:");
    for (Level level : enumSet) {
      System.out.println("  " + level);
    }
    System.out.println("EnumSet contains MEDIUM: " + enumSet.contains(Level.MEDIUM));
  }

  private void EnumMap() {
    // EnumMap – map optimizat pentru enum cheie
    enumMap = new EnumMap<Level, String>(Level.class);
    enumMap.put(Level.LOW, "Low priority");
    enumMap.put(Level.MEDIUM, "Medium priority");
    enumMap.put(Level.HIGH, "High priority");
    System.out.println("EnumMap contents:");
    for (Map.Entry<Level, String> entry : enumMap.entrySet()) {
      System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
    }
  }

  private void ConcurrentHashMap() {
    concurrentHashMap = new ConcurrentHashMap<String, Integer>();
    concurrentHashMap.put("one", 1);
    concurrentHashMap.putIfAbsent("two", 2);
    concurrentHashMap.putIfAbsent("one", 10); // nu suprascrie
    System.out.println("ConcurrentHashMap contents:");
    for (Map.Entry<String, Integer> entry : concurrentHashMap.entrySet()) {
      System.out.println("  " + entry.getKey() + " = " + entry.getValue());
    }
  }

  private void CopyOnWriteArrayList() {
    // CopyOnWriteArrayList – iterare sigură în timp ce modifici
    copyOnWriteArrayList = new CopyOnWriteArrayList<String>();
    copyOnWriteArrayList.add("A");
    copyOnWriteArrayList.add("B");
    System.out.println("CopyOnWriteArrayList iteration:");
    for (String value : copyOnWriteArrayList) {
      System.out.println("  " + value);
      copyOnWriteArrayList.add("X"); // nu aruncă ConcurrentModificationException
      break; // doar ca exemplu, ca să nu crească la infinit
    }
    System.out.println("CopyOnWriteArrayList final size: " + copyOnWriteArrayList.size());
  }

  private void CopyOnWriteArraySet() {
    // CopyOnWriteArraySet – set fără duplicate, cu copy-on-write
    copyOnWriteArraySet = new CopyOnWriteArraySet<String>();
    copyOnWriteArraySet.add("dog");
    copyOnWriteArraySet.add("cat");
    copyOnWriteArraySet.add("dog"); // ignorat
    System.out.println("CopyOnWriteArraySet contents:");
    for (String value : copyOnWriteArraySet) {
      System.out.println("  " + value);
    }
  }

  private void ArrayBlockingQueue() {
    // ArrayBlockingQueue – coadă limitată, blocking
    arrayBlockingQueue = new ArrayBlockingQueue<String>(2);
    arrayBlockingQueue.offer("first");
    arrayBlockingQueue.offer("second");
    arrayBlockingQueue.offer("third");
    System.out.println("ArrayBlockingQueue poll:");
    System.out.println("  " + arrayBlockingQueue.poll());
    System.out.println("  " + arrayBlockingQueue.poll());
    System.out.println("  " + arrayBlockingQueue.poll());
  }

  private void LinkedBlockingQueue() {
    // LinkedBlockingQueue – coadă nelimitată sau cu limită mare
    linkedBlockingQueue = new LinkedBlockingQueue<String>();
    linkedBlockingQueue.offer("job1");
    linkedBlockingQueue.offer("job2");
    System.out.println("LinkedBlockingQueue take:");
    try {
      System.out.println("  " + linkedBlockingQueue.take());
      System.out.println("  " + linkedBlockingQueue.take());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    Thread producer = new Thread(new Producer(linkedBlockingQueue));
    Thread consumer = new Thread(new Consumer(linkedBlockingQueue));
    producer.start();
    consumer.start();
  }

  private void PriorityBlockingQueue() {
    // PriorityBlockingQueue – ordonare naturală a elementelor
    priorityBlockingQueue = new PriorityBlockingQueue<Integer>();
    priorityBlockingQueue.add(30);
    priorityBlockingQueue.add(10);
    priorityBlockingQueue.add(20);
    System.out.println("PriorityBlockingQueue poll (sorted):");
    System.out.println("  " + priorityBlockingQueue.poll());
    System.out.println("  " + priorityBlockingQueue.poll());
    System.out.println("  " + priorityBlockingQueue.poll());
  }

  private void DelayQueue() {
    // DelayQueue – elementele devin vizibile după un delay
    delayQueue = new DelayQueue<DelayedTask>();
    System.out.println("DelayQueue test");
    delayQueue.add(new DelayedTask("task1", 500));
    delayQueue.add(new DelayedTask("task2", 100));
    try {
      DelayedTask task = delayQueue.take(); //wait until delay expire .Will pickup task2
      System.out.println("DelayQueue took: " + task);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void SynchronousQueue() {
    // SynchronousQueue – schimb direct între două thread-uri, fără buffer
    synchronousQueue = new SynchronousQueue<String>();
    Thread consumerSync = new Thread(new Runnable() {
      public void run() {
        try {
          String v = synchronousQueue.take();
          System.out.println("SynchronousQueue received: " + v);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });
    consumerSync.start();
    try {
      synchronousQueue.put("sync-message");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void ConcurrentLinkedQueue() {
    concurrentLinkedQueue = new ConcurrentLinkedQueue();
    concurrentLinkedQueue.offer("a");
    concurrentLinkedQueue.offer("b");
    System.out.println("ConcurrentLinkedQueue poll:");
    System.out.println("  " + concurrentLinkedQueue.poll());
    System.out.println("  " + concurrentLinkedQueue.poll());
  }

  private void ConcurrentLinkedDeque() {
    System.out.println("=== ConcurrentLinkedDeque example ===");
    ConcurrentLinkedDeque<String> newQueue = new ConcurrentLinkedDeque<>();
    newQueue.add("Task-1");
    newQueue.addFirst("Priority-Task");
    newQueue.addLast("Task-2");

    String first = newQueue.pollFirst();
    String last = newQueue.pollLast();

    System.out.println("First consumed: " + first);
    System.out.println("Last consumed:  " + last);
    System.out.println("Remaining size: " + concurrentLinkedDeque.size());
  }

  private void LinkedTransferQueue() throws InterruptedException {
    System.out.println("\n=== LinkedTransferQueue example ===");

    Thread consumer = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          String item = linkedTransferQueue.take();  // waits for a producer
          System.out.println("Consumer received: " + item);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });

    consumer.start();

    // Producer side
    boolean transferred = linkedTransferQueue.tryTransfer("Message-From-Producer", 1, TimeUnit.SECONDS);

    System.out.println("Was message handed off immediately? " + transferred);

    consumer.join();
  }

  enum Level {
    LOW, MEDIUM, HIGH
  }

  static class Producer implements Runnable {

    private final BlockingQueue queue;

    Producer(BlockingQueue q) {
      this.queue = q;
    }

    public void run() {
      try {
        for (int i = 1; i <= 5; i++) {
          String item = "Item-" + i;
          System.out.println("LinkedBlockingQueue. Producing " + item);
          queue.put(item); // blocks if queue is full
          Thread.sleep(500);
        }
        System.out.println("Producer done.");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  static class Consumer implements Runnable {

    private final BlockingQueue queue;

    Consumer(BlockingQueue q) {
      this.queue = q;
    }

    public void run() {
      try {
        for (int i = 1; i <= 5; i++) {
          String item = (String) queue.take(); // blocks if queue is empty
          System.out.println("LinkedBlockingQueue. Consuming " + item);
          Thread.sleep(1000);
        }
        System.out.println("Consumer done.");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  static class DelayedTask implements Delayed {

    private final long endTime;
    private final String name;

    DelayedTask(String name, long delayMillis) {
      this.name = name;
      this.endTime = System.currentTimeMillis() + delayMillis;
    }

    public long getDelay(TimeUnit unit) {
      long diff = endTime - System.currentTimeMillis();
      return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    public int compareTo(Delayed other) {
      DelayedTask o = (DelayedTask) other;
      if (this.endTime == o.endTime) {
        return 0;
      } else if (this.endTime < o.endTime) {
        return -1;
      } else {
        return 1;
      }
    }

    @Override
    public String toString() {
      return "DelayedTask{" + name + "}";
    }

  }


  public static void main(String[] args) throws InterruptedException {
    new JavaDataTypes();
  }

}


