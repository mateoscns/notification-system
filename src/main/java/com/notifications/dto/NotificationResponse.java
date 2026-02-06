package com.notifications.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
    UUID eventId,
    String status,
    String channel,
    String message,
    Instant timestamp
) {
    
    public static NotificationResponse success(UUID eventId, String channel) {
        return new NotificationResponse(
            eventId,
            "ACCEPTED",
            channel,
            "Notificación encolada para procesamiento asíncrono",
            Instant.now()
        );
    }
    
    public static NotificationResponse broadcast(UUID eventId) {
        return new NotificationResponse(
            eventId,
            "ACCEPTED",
            "ALL",
            "Notificación enviada a todos los canales (EMAIL, SMS, WEB)",
            Instant.now()
        );
    }
}
