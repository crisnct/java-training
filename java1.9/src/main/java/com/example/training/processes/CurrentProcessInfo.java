package com.example.training.processes;

public class CurrentProcessInfo {

  public static void main(String[] args) {
    ProcessHandle handle = ProcessHandle.current();

    long pid = handle.pid();
    System.out.println("PID: " + pid);

    ProcessHandle.Info info = handle.info();
    info.command().ifPresent(cmd -> System.out.println("Command: " + cmd));
    info.startInstant().ifPresent(start -> {
      System.out.println("Start time: " + start);
    });
    info.totalCpuDuration().ifPresent(cpu -> {
      System.out.println("Total CPU time: " + cpu);
    });
    info.user().ifPresent(user -> System.out.println("User: " + user));
  }
}
