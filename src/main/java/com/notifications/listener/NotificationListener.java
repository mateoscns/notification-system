package com.notifications.listener;

import com.notifications.config.RabbitMqConfig;
import com.notifications.domain.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationListener {

    @RabbitListener(queues = RabbitMqConfig.EMAIL_QUEUE)
    public void handleEmailNotification(NotificationEvent event) {
        log.info("📧 [EMAIL] Processing notification [eventId={}, userId={}]",
                event.uuid(),
                event.userId());
        
        try {
            // Simula latencia de envío a servidor SMTP
            Thread.sleep(1500);
            
            // Aquí iría la integración real con SendGrid/SES
            log.info("📧 [EMAIL] ✓ Notification sent successfully [eventId={}, payload='{}']",
                    event.uuid(),
                    truncatePayload(event.payload()));
                    
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("📧 [EMAIL] ✗ Processing interrupted [eventId={}]", event.uuid());
            throw new RuntimeException("Email processing interrupted", e);
        }
    }

    @RabbitListener(queues = RabbitMqConfig.SMS_QUEUE)
    public void handleSmsNotification(NotificationEvent event) {
        log.info("📱 [SMS] Processing notification [eventId={}, userId={}]",
                event.uuid(),
                event.userId());
        
        try {
            // Simula latencia de API de Twilio/SNS
            Thread.sleep(800);
            
            // Aquí iría la integración real con Twilio o AWS SNS
            log.info("📱 [SMS] ✓ Notification sent successfully [eventId={}, payload='{}']",
                    event.uuid(),
                    truncatePayload(event.payload()));
                    
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("📱 [SMS] ✗ Processing interrupted [eventId={}]", event.uuid());
            throw new RuntimeException("SMS processing interrupted", e);
        }
    }

    @RabbitListener(queues = RabbitMqConfig.WEB_QUEUE)
    public void handleWebNotification(NotificationEvent event) {
        log.info("🌐 [WEB] Processing notification [eventId={}, userId={}]",
                event.uuid(),
                event.userId());
        
        try {
            // Simula latencia de Firebase Cloud Messaging
            Thread.sleep(500);
            
            // Aquí iría la integración real con FCM/APNs
            log.info("🌐 [WEB] ✓ Notification sent successfully [eventId={}, payload='{}']",
                    event.uuid(),
                    truncatePayload(event.payload()));
                    
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("🌐 [WEB] ✗ Processing interrupted [eventId={}]", event.uuid());
            throw new RuntimeException("Web Push processing interrupted", e);
        }
    }

    private String truncatePayload(String payload) {
        if (payload == null) return "null";
        return payload.length() > 50 
            ? payload.substring(0, 50) + "..." 
            : payload;
    }
}
