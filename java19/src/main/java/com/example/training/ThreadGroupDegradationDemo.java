package com.example.training;

//@formatter:off
// Demo: ThreadGroup has no lifecycle control in JDK 19+
// Only grouping metadata remains meaningful: name, activeCount, activeGroupCount.
// ThreadGroup is no longer a thread management API.
// JVM devs intentionally gutted the dangerous methods (stop/resume/suspend/destroy).
// It exists only to not break ancient code that still references groups.
//@formatter:on
public class ThreadGroupDegradationDemo {

  public static void main(String[] args) throws Exception {
    ThreadGroup group = new ThreadGroup("demo-group");

    Thread t1 = new Thread(group, () -> System.out.println("Task 1"));
    Thread t2 = new Thread(group, () -> System.out.println("Task 2"));

    t1.start();
    t2.start();
    t1.join();
    t2.join();

    // Still usable: simple metadata
    System.out.println("Thread group name: " + group.getName());
    System.out.println("Active threads in group: " + group.activeCount());
    System.out.println("Active subgroups in group: " + group.activeGroupCount());

    // There is no valid thread-control API anymore.
    // Methods like stop(), suspend(), resume(), destroy() have been removed or are non-functional.
    // ThreadGroup should only be treated as a label/grouping, not a management tool.
  }
}
