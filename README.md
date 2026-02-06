# Sistema de Notificaciones Multicanal con RabbitMQ

Sistema escalable de notificaciones asíncronas usando Spring Boot 3 y RabbitMQ con arquitectura basada en eventos.

## Arquitectura

```
┌─────────────────┐      ┌──────────────────────────────────────────┐
│   REST API      │      │           RabbitMQ (CloudAMQP)           │
│                 │      │  ┌────────────────────────────────────┐  │
│  POST /api/v1/  │─────▶│  │     Topic Exchange                 │  │
│  notifications  │      │  │   "notifications.exchange"         │  │
└─────────────────┘      │  └────────────────┬───────────────────┘  │
                         │                   │                       │
                         │    ┌──────────────┼──────────────┐       │
                         │    │              │              │       │
                         │    ▼              ▼              ▼       │
                         │ ┌──────┐     ┌──────┐      ┌──────┐     │
                         │ │EMAIL │     │ SMS  │      │ WEB  │     │
                         │ │Queue │     │Queue │      │Queue │     │
                         │ └──┬───┘     └──┬───┘      └──┬───┘     │
                         └────┼────────────┼────────────┼──────────┘
                              │            │            │
                              ▼            ▼            ▼
                         ┌─────────────────────────────────────────┐
                         │           NotificationListener           │
                         │  ┌─────────┐ ┌─────────┐ ┌─────────┐   │
                         │  │ Email   │ │   SMS   │ │   Web   │   │
                         │  │ Handler │ │ Handler │ │ Handler │   │
                         │  └─────────┘ └─────────┘ └─────────┘   │
                         └─────────────────────────────────────────┘
```

## Tecnologías

- Java 17
- Spring Boot 3.2.2
- Spring AMQP (RabbitMQ)
- CloudAMQP (AWS)
- Lombok
- Jackson (JSON)

## Estructura del Proyecto

```
src/main/java/com/notifications/
├── config/
│   ├── RabbitMqConfig.java      # Exchange, Queues, Bindings
│   └── JacksonConfig.java       # JSON serialization
├── controller/
│   ├── NotificationController.java
│   ├── BulkNotificationController.java
│   └── GlobalExceptionHandler.java
├── service/
│   ├── NotificationService.java
│   └── BulkNotificationService.java
├── listener/
│   └── NotificationListener.java  # Consumers (3 canales)
├── domain/
│   ├── NotificationChannel.java   # Enum con routing keys
│   ├── NotificationEvent.java     # Domain Event (Record)
│   └── NotificationStatus.java    # Estado de entrega
└── dto/
    ├── NotificationRequest.java   # Request DTO (Record)
    ├── NotificationResponse.java
    ├── BulkNotificationRequest.java
    └── BulkNotificationResponse.java
```

## Configuración CloudAMQP

La aplicación está configurada para conectarse a CloudAMQP (AWS):

```properties
spring.rabbitmq.host=chimpanzee.rmq.cloudamqp.com
spring.rabbitmq.port=5671
spring.rabbitmq.username=slvokyqa
spring.rabbitmq.virtual-host=slvokyqa
spring.rabbitmq.ssl.enabled=true
```

**RabbitMQ Manager:** https://chimpanzee.rmq.cloudamqp.com

## Ejecutar

```bash
mvn spring-boot:run
```

O ejecutar `NotificationSystemApplication.java` desde el IDE.

## API Endpoints

### Enviar Notificación Individual

```
POST /api/v1/notifications
Content-Type: application/json

{
  "userId": "user123",
  "message": "Tu pedido ha sido enviado",
  "channel": "EMAIL"
}
```

**Canales disponibles:** `EMAIL`, `SMS`, `WEB`, `ALL`

### Broadcast a Todos los Canales

```
POST /api/v1/notifications/broadcast?userId=user123&message=Mensaje importante
```

### Envío Masivo (Bulk)

```
POST /api/v1/notifications/bulk
Content-Type: application/json

{
  "userIds": ["user1", "user2", "user3"],
  "message": "Promoción especial",
  "channels": ["EMAIL", "SMS"]
}
```

### Health Check

```
GET /api/v1/notifications/health
```

## Routing Keys

| Canal | Routing Key | Cola Destino |
|-------|-------------|--------------|
| EMAIL | notify.email | notifications.email.queue |
| SMS | notify.sms | notifications.sms.queue |
| WEB | notify.web | notifications.web.queue |
| ALL | notify.all | Todas las colas (broadcast) |

## Demo - Comandos cURL (Windows PowerShell)

```powershell
# 📧 EMAIL
curl -X POST http://localhost:8080/api/v1/notifications -H "Content-Type: application/json" -d "{\"userId\":\"user-001\",\"message\":\"Bienvenido a nuestro servicio\",\"channel\":\"EMAIL\"}"

# 📱 SMS
curl -X POST http://localhost:8080/api/v1/notifications -H "Content-Type: application/json" -d "{\"userId\":\"user-001\",\"message\":\"Tu codigo es 123456\",\"channel\":\"SMS\"}"

# 🌐 WEB PUSH
curl -X POST http://localhost:8080/api/v1/notifications -H "Content-Type: application/json" -d "{\"userId\":\"user-001\",\"message\":\"Nueva promocion disponible\",\"channel\":\"WEB\"}"

# 📢 BROADCAST (todos los canales)
curl -X POST http://localhost:8080/api/v1/notifications -H "Content-Type: application/json" -d "{\"userId\":\"user-001\",\"message\":\"Alerta importante\",\"channel\":\"ALL\"}"

# 📦 BULK (envío masivo)
curl -X POST http://localhost:8080/api/v1/notifications/bulk -H "Content-Type: application/json" -d "{\"userIds\":[\"user-001\",\"user-002\",\"user-003\"],\"message\":\"Promocion especial\",\"channels\":[\"EMAIL\",\"SMS\"]}"

# ❤️ HEALTH CHECK
curl http://localhost:8080/api/v1/notifications/health
```

## Características de Escalabilidad

- **Concurrencia configurable:** 3-10 consumers por cola
- **Prefetch:** 10 mensajes por consumer
- **Retry automático:** 3 intentos con backoff exponencial
- **SSL/TLS:** Conexión segura a CloudAMQP
- **Topic Exchange:** Enrutamiento flexible con routing keys

## Tiempos de Procesamiento Simulados

| Canal | Latencia Simulada |
|-------|-------------------|
| EMAIL | 1500ms |
| SMS | 800ms |
| WEB | 500ms |

Esto permite demostrar el procesamiento asíncrono y paralelo de los 3 canales.
