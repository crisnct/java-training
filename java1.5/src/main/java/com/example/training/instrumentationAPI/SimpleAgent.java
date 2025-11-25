package com.example.training.instrumentationAPI;

import java.lang.instrument.Instrumentation;

/**
 * Agentul se pornește cu -javaagent:simple-agent.jar în linia de comandă, specific Java 1.5.)
 */
public class SimpleAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        Class[] classes = inst.getAllLoadedClasses();
        System.out.println("Agent loaded. Loaded classes: " + classes.length);
    }
}
