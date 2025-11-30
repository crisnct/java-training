package com.example.training.processes;

public class ForceKillByPid {
    public static void main(String[] args) {
        long pidToKill = 35688;

        ProcessHandle.of(pidToKill).ifPresent(ph -> {
            System.out.println("Force killing process " + pidToKill);
            boolean forced = ph.destroyForcibly();
            System.out.println("destroyForcibly() returned: " + forced);
        });
    }
}
