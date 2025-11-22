package com.example.training.modern;

import java.util.Objects;

public class ModernMessenger {

    private final Notifier notifier;

    public ModernMessenger(Notifier notifier) {
        this.notifier = Objects.requireNonNull(notifier, "notifier");
    }

    public void sendPersonalGreeting(String name) {
        String normalized = normalizeName(name);
        Message message = new Message(normalized, "Hello, %s!".formatted(normalized));
        notifier.send(message);
    }

    private String normalizeName(String name) {
        if (name == null) {
            return "friend";
        }
        String trimmed = name.trim();
        return trimmed.isBlank() ? "friend" : trimmed;
    }
}
