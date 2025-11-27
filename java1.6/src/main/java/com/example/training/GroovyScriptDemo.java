package com.example.training;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class GroovyScriptDemo {

  public static void main(String[] args) throws Exception {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("groovy");

    // Share Java variables with Groovy
    engine.put("name", "Cris");
    engine.put("a", 6);
    engine.put("b", 9);

    // Groovy script (can be multi-line)
    String script =
        "println \"Hello, $name! Running Groovy inside Java.\"\n" +
            "def multiply(x, y) { x * y }\n" +
            "def result = multiply(a, b) + 10\n" +
            "return result";

    // Execute the Groovy code and get the result
    Object result = engine.eval(script);

    System.out.println("Result from Groovy: " + result); // Expected: 64
    engine.eval("print('Hello from JavaScript inside Java!');");
  }
}
