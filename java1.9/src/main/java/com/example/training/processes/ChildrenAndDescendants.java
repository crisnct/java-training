package com.example.training.processes;

public class ChildrenAndDescendants {

  public static void main(String[] args) {
    ProcessHandle current = ProcessHandle.current();

    System.out.println("Current PID: " + current.pid());

    System.out.println("Children:");
    current.children().forEach(ph ->
        System.out.println("  Child PID: " + ph.pid()));

    System.out.println("Descendants:");
    current.descendants().forEach(ph ->
        System.out.println("  Descendant PID: " + ph.pid()));
  }
}
