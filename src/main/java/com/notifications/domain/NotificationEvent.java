package com.notifications.domain;

import java.time.Instant;
import java.util.UUID;

// Record inmutable que representa un evento de notificación en el dominio
public record NotificationEvent(
    UUID uuid,
    String userId,
    String payload,
    Instant timestamp,
    NotificationChannel channel
) {
    
    // Factory method que genera UUID y timestamp automáticamente
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
