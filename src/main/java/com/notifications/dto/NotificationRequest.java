package com.notifications.dto;

import com.notifications.domain.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificationRequest(
    
    @NotBlank(message = "El userId es obligatorio")
    String userId,
    
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 1000, message = "El mensaje no puede exceder los 1000 caracteres")
    String message,
    
    @NotNull(message = "Debe especificar un canal (EMAIL, SMS, WEB, ALL)")
    NotificationChannel channel
    
) {}
