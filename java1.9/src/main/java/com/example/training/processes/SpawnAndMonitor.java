package com.example.training.processes;

import java.io.IOException;

public class SpawnAndMonitor {

  public static void main(String[] args) throws IOException, InterruptedException {
    // Simple example: list current directory
    Process process = new ProcessBuilder("cmd", "/c", "dir") // on Linux: "ls", "-l"
        .inheritIO()     // inherit stdout/stderr
        .start();

    ProcessHandle handle = process.toHandle();

    System.out.println("Spawned PID: " + handle.pid());

    // Synchronous wait
    int exitCode = process.waitFor();
    System.out.println("Process exited with code: " + exitCode);
  }
}
