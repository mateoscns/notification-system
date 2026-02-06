package com.notifications.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI notificationSystemOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification System API")
                        .description("Sistema de Notificaciones Multicanal - RabbitMQ + Spring Boot 3")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Notification Team")
                                .email("notifications@example.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local")
                ));
    }
}
