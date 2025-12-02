package com.example.trining;

// Not exported → totally inaccessible from other modules
class InternalLogic {
    String format(String name) {
        return "Hello, " + name;
    }
}
