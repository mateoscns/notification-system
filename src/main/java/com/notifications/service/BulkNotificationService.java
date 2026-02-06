package com.notifications.service;

import com.notifications.config.RabbitMqConfig;
import com.notifications.domain.NotificationChannel;
import com.notifications.domain.NotificationEvent;
import com.notifications.dto.BulkNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BulkNotificationService {

    private final RabbitTemplate rabbitTemplate;

    public List<NotificationEvent> sendBulkNotifications(BulkNotificationRequest request) {
        
        // Genera ID de lote para tracking
        UUID batchId = UUID.randomUUID();
        List<NotificationEvent> events = new ArrayList<>();
        
        log.info("Processing bulk notification [batchId={}, users={}, channels={}]",
                batchId,
                request.userIds().size(),
                request.channels().size());
        
        // Itera usuarios x canales para crear y publicar eventos
        for (String userId : request.userIds()) {
            for (NotificationChannel channel : request.channels()) {
                NotificationEvent event = NotificationEvent.create(
                    userId,
                    request.message(),
                    channel
                );
                
                // Publica cada evento al canal correspondiente
                rabbitTemplate.convertAndSend(
                    RabbitMqConfig.EXCHANGE_NAME,
                    channel.getRoutingKey(),
                    event
                );
                
                events.add(event);
            }
        }
        
        log.info("Bulk notification completed [batchId={}, totalEvents={}]",
                batchId,
                events.size());
        
        return events;
    }
}
