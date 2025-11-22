package com.example.training.modern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModernMessengerTest {

    @Mock
    Notifier notifier;

    @Test
    void sendsGreetingUsingNotifier() {
        ModernMessenger messenger = new ModernMessenger(notifier);

        messenger.sendPersonalGreeting("Coder");

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(notifier).send(captor.capture());

        Message captured = captor.getValue();
        assertEquals("Coder", captured.recipient());
        assertEquals("Hello, Coder!", captured.body());
    }

    @Test
    void defaultsToFriendWhenNameMissing() {
        ModernMessenger messenger = new ModernMessenger(notifier);

        messenger.sendPersonalGreeting("  ");

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(notifier).send(captor.capture());

        Message captured = captor.getValue();
        assertEquals("friend", captured.recipient());
        assertEquals("Hello, friend!", captured.body());
    }
}
