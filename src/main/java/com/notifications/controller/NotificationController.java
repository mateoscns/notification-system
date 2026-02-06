package com.notifications.controller;

import com.notifications.domain.NotificationChannel;
import com.notifications.domain.NotificationEvent;
import com.notifications.dto.NotificationRequest;
import com.notifications.dto.NotificationResponse;
import com.notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notifications", description = "API de Notificaciones Multicanal")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Enviar notificación", description = "Envía una notificación a un canal específico (EMAIL, SMS, WEB) o a todos (ALL)")
    @ApiResponse(responseCode = "202", description = "Notificación aceptada para procesamiento asíncrono")
    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(
            @Valid @RequestBody NotificationRequest request) {
        
        // Registra la solicitud recibida
        log.info("Received notification request [userId={}, channel={}]",
                request.userId(), 
                request.channel());
        
        // Convierte DTO a evento de dominio y publica en RabbitMQ
        NotificationEvent event = notificationService.sendNotification(request);
        
        NotificationResponse response = request.channel() == NotificationChannel.ALL
            ? NotificationResponse.broadcast(event.uuid())
            : NotificationResponse.success(event.uuid(), request.channel().name());
        
        log.info("Notification accepted [eventId={}]", event.uuid());
        
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }
    
    @Operation(summary = "Broadcast", description = "Envía una notificación a todos los canales simultáneamente")
    @ApiResponse(responseCode = "202", description = "Broadcast aceptado")
    @PostMapping("/broadcast")
    public ResponseEntity<NotificationResponse> broadcastNotification(
            @RequestParam String userId,
            @RequestParam String message) {
        
        // Registra solicitud de broadcast
        log.info("Received broadcast request [userId={}]", userId);
        
        // Publica a todos los canales en paralelo
        NotificationEvent event = notificationService.broadcastNotification(userId, message);
        
        NotificationResponse response = NotificationResponse.broadcast(event.uuid());
        
        log.info("Broadcast accepted [eventId={}]", event.uuid());
        
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }

    @Operation(summary = "Health Check", description = "Verifica el estado del servicio")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        // Retorna estado UP con los 3 canales disponibles
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "notification-system",
            "channels", new String[]{"EMAIL", "SMS", "WEB"}
        ));
    }
}
