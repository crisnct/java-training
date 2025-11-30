package com.example.training.processes;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class AsyncProcessMonitor {

  public static void main(String[] args) throws IOException {
    Process process = new ProcessBuilder("ping", "-c", "10", "google.com") // Linux/mac
        //.command("ping", "localhost") // Windows alternative
        .start();

    ProcessHandle handle = process.toHandle();
    System.out.println("Started PID: " + handle.pid());

    CompletableFuture<ProcessHandle> onExit = handle.onExit();

    onExit.thenAccept(ph -> {
      System.out.println("Process " + ph.pid() + " has exited.");
      ph.info().totalCpuDuration().ifPresent(cpu ->
          System.out.println("Total CPU time: " + cpu));
    });

    // Keep main alive long enough; in real apps, use a proper lifecycle
    onExit.join();
  }
}
