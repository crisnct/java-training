package com.example.training.processes;

import java.util.stream.Stream;

public class ListProcesses {

  public static void main(String[] args) {
    Stream<ProcessHandle> processes = ProcessHandle.allProcesses();

    processes
        .filter(ph -> ph.info().command().isPresent())
        .limit(10) // just to not flood the console
        .forEach(ph -> {
          ProcessHandle.Info info = ph.info();
          System.out.println("PID: " + ph.pid());

          info.command().ifPresent(cmd ->
              System.out.println("  Command: " + cmd));

          info.startInstant().ifPresent(start ->
              System.out.println("  Started: " + start));

          info.totalCpuDuration().ifPresent(cpu ->
              System.out.println("  CPU: " + cpu));

          System.out.println();
        });
  }
}
