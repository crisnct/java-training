package com.example.training;

import javax.script.*;

public class JavaScriptDemo {
    public static void main(String[] args) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        engine.eval("print('Hello from JavaScript inside Java!');");
    }
}
