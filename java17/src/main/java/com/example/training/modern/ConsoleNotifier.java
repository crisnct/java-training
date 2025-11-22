package com.example.training.modern;

import java.io.PrintStream;
import java.util.Objects;

public class ConsoleNotifier implements Notifier {

    private final PrintStream output;

    public ConsoleNotifier(PrintStream output) {
        this.output = Objects.requireNonNull(output, "output");
    }

    @Override
    public void send(Message message) {
        Message safeMessage = Objects.requireNonNull(message, "message");
        output.printf("[%s] %s%n", safeMessage.recipient(), safeMessage.body());
    }
}
