package com.notifications.domain;

import java.time.Instant;
import java.util.UUID;

public record NotificationStatus(
    UUID eventId,
    NotificationChannel channel,
    DeliveryStatus status,
    Instant processedAt,
    String processorId
) {
    
    public enum DeliveryStatus {
        QUEUED,
        PROCESSING,
        DELIVERED,
        FAILED,
        RETRYING
    }
    
    public static NotificationStatus delivered(UUID eventId, NotificationChannel channel) {
        return new NotificationStatus(
            eventId,
            channel,
            DeliveryStatus.DELIVERED,
            Instant.now(),
            Thread.currentThread().getName()
        );
    }
    
    public static NotificationStatus failed(UUID eventId, NotificationChannel channel) {
        return new NotificationStatus(
            eventId,
            channel,
            DeliveryStatus.FAILED,
            Instant.now(),
            Thread.currentThread().getName()
        );
    }
}
