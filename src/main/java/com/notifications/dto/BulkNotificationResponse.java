package com.notifications.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BulkNotificationResponse(
    UUID batchId,
    String status,
    int totalNotifications,
    List<String> channels,
    String message,
    Instant timestamp
) {
    
    public static BulkNotificationResponse success(UUID batchId, int total, List<String> channels) {
        return new BulkNotificationResponse(
            batchId,
            "ACCEPTED",
            total,
            channels,
            "Notificaciones masivas encoladas para procesamiento",
            Instant.now()
        );
    }
}
