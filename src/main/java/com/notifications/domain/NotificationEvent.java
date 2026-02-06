package com.notifications.domain;

import java.time.Instant;
import java.util.UUID;

public record NotificationEvent(
    UUID uuid,
    String userId,
    String payload,
    Instant timestamp,
    NotificationChannel channel
) {
    
    public static NotificationEvent create(String userId, String payload, NotificationChannel channel) {
        return new NotificationEvent(
            UUID.randomUUID(),
            userId,
            payload,
            Instant.now(),
            channel
        );
    }
}
