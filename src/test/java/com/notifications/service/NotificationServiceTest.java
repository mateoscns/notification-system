package com.notifications.service;

import com.notifications.config.RabbitMqConfig;
import com.notifications.domain.NotificationChannel;
import com.notifications.domain.NotificationEvent;
import com.notifications.dto.NotificationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("Should publish EMAIL notification with correct routing key")
    void sendEmailNotification() {
        NotificationRequest request = new NotificationRequest("user-001", "Test Email", NotificationChannel.EMAIL);

        NotificationEvent event = notificationService.sendNotification(request);

        assertNotNull(event.uuid());
        assertEquals("user-001", event.userId());
        assertEquals("Test Email", event.payload());
        assertEquals(NotificationChannel.EMAIL, event.channel());

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMqConfig.EXCHANGE_NAME),
                eq("notify.email"),
                eq(event)
        );
    }

    @Test
    @DisplayName("Should publish SMS notification with correct routing key")
    void sendSmsNotification() {
        NotificationRequest request = new NotificationRequest("user-002", "Test SMS", NotificationChannel.SMS);

        NotificationEvent event = notificationService.sendNotification(request);

        assertEquals("user-002", event.userId());
        assertEquals(NotificationChannel.SMS, event.channel());

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMqConfig.EXCHANGE_NAME),
                eq("notify.sms"),
                eq(event)
        );
    }

    @Test
    @DisplayName("Should publish WEB notification with correct routing key")
    void sendWebNotification() {
        NotificationRequest request = new NotificationRequest("user-003", "Test Web", NotificationChannel.WEB);

        NotificationEvent event = notificationService.sendNotification(request);

        assertEquals("user-003", event.userId());
        assertEquals(NotificationChannel.WEB, event.channel());

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMqConfig.EXCHANGE_NAME),
                eq("notify.web"),
                eq(event)
        );
    }

    @Test
    @DisplayName("Should broadcast notification using ALL routing key")
    void broadcastNotification() {
        NotificationEvent event = notificationService.broadcastNotification("user-004", "Broadcast message");

        assertNotNull(event.uuid());
        assertEquals("user-004", event.userId());
        assertEquals(NotificationChannel.ALL, event.channel());

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMqConfig.EXCHANGE_NAME),
                eq(RabbitMqConfig.BROADCAST_ROUTING_KEY),
                eq(event)
        );
    }

    @Test
    @DisplayName("Should generate unique UUID for each event")
    void uniqueEventIds() {
        NotificationRequest request1 = new NotificationRequest("user-001", "Msg 1", NotificationChannel.EMAIL);
        NotificationRequest request2 = new NotificationRequest("user-001", "Msg 2", NotificationChannel.EMAIL);

        NotificationEvent event1 = notificationService.sendNotification(request1);
        NotificationEvent event2 = notificationService.sendNotification(request2);

        assertNotEquals(event1.uuid(), event2.uuid());
    }

    @Test
    @DisplayName("Should publish to specific routing key using publishToChannel")
    void publishToChannel() {
        NotificationEvent event = NotificationEvent.create("user-005", "Direct msg", NotificationChannel.EMAIL);
        String customRoutingKey = "notify.custom";

        notificationService.publishToChannel(event, customRoutingKey);

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMqConfig.EXCHANGE_NAME),
                eq(customRoutingKey),
                eq(event)
        );
    }
}
