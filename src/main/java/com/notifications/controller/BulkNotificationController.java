package com.notifications.controller;

import com.notifications.domain.NotificationEvent;
import com.notifications.dto.BulkNotificationRequest;
import com.notifications.dto.BulkNotificationResponse;
import com.notifications.service.BulkNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications/bulk")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bulk Notifications", description = "API de Notificaciones Masivas")
public class BulkNotificationController {

    private final BulkNotificationService bulkNotificationService;

    @Operation(summary = "Envío masivo", description = "Envía notificaciones a múltiples usuarios y canales simultáneamente")
    @ApiResponse(responseCode = "202", description = "Notificaciones masivas aceptadas")
    @PostMapping
    public ResponseEntity<BulkNotificationResponse> sendBulkNotification(
            @Valid @RequestBody BulkNotificationRequest request) {
        log.info("Received bulk notification request [users={}, channels={}]",
                request.userIds().size(),
                request.channels().size());
        
        List<NotificationEvent> events = bulkNotificationService.sendBulkNotifications(request);
        
        List<String> channels = request.channels().stream()
                .map(Enum::name)
                .toList();
        
        BulkNotificationResponse response = BulkNotificationResponse.success(
            UUID.randomUUID(),
            events.size(),
            channels
        );
        
        log.info("Bulk notification accepted [total={}]", events.size());
        
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }
}
