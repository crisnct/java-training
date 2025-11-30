package com.example.training.processes;

public class KillByPid {
    public static void main(String[] args) {
        long pidToKill = 35240; // example

        ProcessHandle.of(pidToKill).ifPresent(ph -> {
            System.out.println("Found process " + pidToKill);
            boolean gentle = ph.destroy(); // sends normal termination signal
            System.out.println("destroy() returned: " + gentle);
        });
    }
}
