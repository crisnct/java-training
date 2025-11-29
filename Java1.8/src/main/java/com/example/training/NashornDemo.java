package com.example.training;

import javax.script.*;

public class NashornDemo {
    public static void main(String[] args) throws Exception {
        //nashorn removed in java 15
        ScriptEngine e = new ScriptEngineManager().getEngineByName("nashorn");
        e.put("x", 10);
        System.out.println(e.eval("x * 2 + 1")); // 21
    }
}
