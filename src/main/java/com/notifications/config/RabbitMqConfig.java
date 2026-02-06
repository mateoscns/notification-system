package com.notifications.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_NAME = "notifications.exchange";
    
    public static final String EMAIL_QUEUE = "notifications.email.queue";
    public static final String SMS_QUEUE = "notifications.sms.queue";
    public static final String WEB_QUEUE = "notifications.web.queue";
    
    public static final String EMAIL_ROUTING_KEY = "notify.email";
    public static final String SMS_ROUTING_KEY = "notify.sms";
    public static final String WEB_ROUTING_KEY = "notify.web";
    public static final String BROADCAST_ROUTING_KEY = "notify.all";

    @Bean
    public TopicExchange notificationsExchange() {
        return ExchangeBuilder
                .topicExchange(EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder
                .durable(EMAIL_QUEUE)
                .build();
    }

    @Bean
    public Queue smsQueue() {
        return QueueBuilder
                .durable(SMS_QUEUE)
                .build();
    }

    @Bean
    public Queue webQueue() {
        return QueueBuilder
                .durable(WEB_QUEUE)
                .build();
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange notificationsExchange) {
        return BindingBuilder
                .bind(emailQueue)
                .to(notificationsExchange)
                .with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public Binding emailBroadcastBinding(Queue emailQueue, TopicExchange notificationsExchange) {
        return BindingBuilder
                .bind(emailQueue)
                .to(notificationsExchange)
                .with(BROADCAST_ROUTING_KEY);
    }

    @Bean
    public Binding smsBinding(Queue smsQueue, TopicExchange notificationsExchange) {
        return BindingBuilder
                .bind(smsQueue)
                .to(notificationsExchange)
                .with(SMS_ROUTING_KEY);
    }

    @Bean
    public Binding smsBroadcastBinding(Queue smsQueue, TopicExchange notificationsExchange) {
        return BindingBuilder
                .bind(smsQueue)
                .to(notificationsExchange)
                .with(BROADCAST_ROUTING_KEY);
    }

    @Bean
    public Binding webBinding(Queue webQueue, TopicExchange notificationsExchange) {
        return BindingBuilder
                .bind(webQueue)
                .to(notificationsExchange)
                .with(WEB_ROUTING_KEY);
    }

    @Bean
    public Binding webBroadcastBinding(Queue webQueue, TopicExchange notificationsExchange) {
        return BindingBuilder
                .bind(webQueue)
                .to(notificationsExchange)
                .with(BROADCAST_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, 
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        template.setExchange(EXCHANGE_NAME);
        return template;
    }
}
