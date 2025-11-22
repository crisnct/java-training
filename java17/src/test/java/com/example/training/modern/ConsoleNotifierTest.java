package com.example.training.modern;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsoleNotifierTest {

    @Test
    void printsFormattedMessage() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ConsoleNotifier notifier = new ConsoleNotifier(new PrintStream(buffer));

        notifier.send(new Message("Alice", "Hello!"));

        assertEquals("[Alice] Hello!" + System.lineSeparator(), buffer.toString(StandardCharsets.UTF_8));
    }
}
