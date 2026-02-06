package com.notifications.dto;

import com.notifications.domain.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record BulkNotificationRequest(
    
    @NotEmpty(message = "Debe especificar al menos un userId")
    Set<String> userIds,
    
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 1000, message = "El mensaje no puede exceder los 1000 caracteres")
    String message,
    
    @NotEmpty(message = "Debe especificar al menos un canal")
    Set<NotificationChannel> channels
    
) {}
