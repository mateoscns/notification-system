package com.notifications.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class NotificationChannelTest {

    @ParameterizedTest
    @EnumSource(NotificationChannel.class)
    @DisplayName("Each channel should have a routing key starting with 'notify.'")
    void routingKeyFormat(NotificationChannel channel) {
        assertTrue(channel.getRoutingKey().startsWith("notify."));
    }

    @Test
    @DisplayName("EMAIL channel should have routing key 'notify.email'")
    void emailRoutingKey() {
        assertEquals("notify.email", NotificationChannel.EMAIL.getRoutingKey());
    }

    @Test
    @DisplayName("SMS channel should have routing key 'notify.sms'")
    void smsRoutingKey() {
        assertEquals("notify.sms", NotificationChannel.SMS.getRoutingKey());
    }

    @Test
    @DisplayName("WEB channel should have routing key 'notify.web'")
    void webRoutingKey() {
        assertEquals("notify.web", NotificationChannel.WEB.getRoutingKey());
    }

    @Test
    @DisplayName("ALL channel should have routing key 'notify.all'")
    void allRoutingKey() {
        assertEquals("notify.all", NotificationChannel.ALL.getRoutingKey());
    }

    @Test
    @DisplayName("Should have exactly 4 channels")
    void channelCount() {
        assertEquals(4, NotificationChannel.values().length);
    }
}
