package com.notifications.service;

import com.notifications.config.RabbitMqConfig;
import com.notifications.domain.NotificationChannel;
import com.notifications.domain.NotificationEvent;
import com.notifications.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final RabbitTemplate rabbitTemplate;

    public NotificationEvent sendNotification(NotificationRequest request) {
        
        // Crea evento de dominio con UUID único y timestamp
        NotificationEvent event = NotificationEvent.create(
            request.userId(),
            request.message(),
            request.channel()
        );
        
        // Obtiene la routing key según el canal
        String routingKey = request.channel().getRoutingKey();
        
        log.info("Publishing notification [eventId={}, channel={}, routingKey={}, userId={}]",
                event.uuid(), 
                request.channel(), 
                routingKey,
                request.userId());
        
        // Publica en RabbitMQ al exchange especificado
        rabbitTemplate.convertAndSend(
            RabbitMqConfig.EXCHANGE_NAME,
            routingKey,
            event
        );
        
        log.info("Notification published successfully [eventId={}]", event.uuid());
        
        return event;
    }
    
    public NotificationEvent broadcastNotification(String userId, String message) {
        
        // Crea evento con canal ALL para ser enrutado a todas las colas
        NotificationEvent event = NotificationEvent.create(
            userId,
            message,
            NotificationChannel.ALL
        );
        
        log.info("Broadcasting notification to ALL channels [eventId={}, userId={}]",
                event.uuid(), 
                userId);
        
        // Publica con routing key especial que llega a los 3 canales
        rabbitTemplate.convertAndSend(
            RabbitMqConfig.EXCHANGE_NAME,
            RabbitMqConfig.BROADCAST_ROUTING_KEY,
            event
        );
        
        log.info("Broadcast notification sent [eventId={}]", event.uuid());
        
        return event;
    }
    
    public void publishToChannel(NotificationEvent event, String routingKey) {
        log.info("Publishing event directly [eventId={}, routingKey={}]", 
                event.uuid(), routingKey);
        
        rabbitTemplate.convertAndSend(
            RabbitMqConfig.EXCHANGE_NAME,
            routingKey,
            event
        );
    }
}
