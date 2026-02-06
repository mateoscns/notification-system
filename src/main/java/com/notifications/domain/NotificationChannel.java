package com.notifications.domain;

public enum NotificationChannel {
    
    EMAIL("notify.email"),
    SMS("notify.sms"),
    WEB("notify.web"),
    ALL("notify.all");
    
    private final String routingKey;
    
    NotificationChannel(String routingKey) {
        this.routingKey = routingKey;
    }
    
    public String getRoutingKey() {
        return routingKey;
    }
}
