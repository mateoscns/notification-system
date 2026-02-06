package com.notifications.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationEventTest {

    @Test
    @DisplayName("Factory method should create event with generated UUID and timestamp")
    void createEvent() {
        NotificationEvent event = NotificationEvent.create("user-001", "Hello", NotificationChannel.EMAIL);

        assertNotNull(event.uuid());
        assertNotNull(event.timestamp());
        assertEquals("user-001", event.userId());
        assertEquals("Hello", event.payload());
        assertEquals(NotificationChannel.EMAIL, event.channel());
    }

    @Test
    @DisplayName("Each event should have a unique UUID")
    void uniqueUuids() {
        NotificationEvent event1 = NotificationEvent.create("user-001", "Msg 1", NotificationChannel.EMAIL);
        NotificationEvent event2 = NotificationEvent.create("user-001", "Msg 2", NotificationChannel.EMAIL);

        assertNotEquals(event1.uuid(), event2.uuid());
    }

    @Test
    @DisplayName("Event should be immutable (Record)")
    void immutableRecord() {
        NotificationEvent event = NotificationEvent.create("user-001", "Test", NotificationChannel.SMS);

        assertEquals(event, new NotificationEvent(
                event.uuid(),
                event.userId(),
                event.payload(),
                event.timestamp(),
                event.channel()
        ));
    }
}
