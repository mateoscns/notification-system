# 📬 Notification System - Event-Driven Architecture

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-6DB33F?style=flat-square&logo=springboot)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-CloudAMQP-FF6600?style=flat-square&logo=rabbitmq)

Sistema escalable de notificaciones multicanal implementando **arquitectura basada en eventos** con Spring Boot 3 y RabbitMQ. Diseñado para demostrar patrones de mensajería asíncrona, desacoplamiento de servicios y procesamiento distribuido.

## ✨ Features

- **Mensajería Asíncrona** - Procesamiento no bloqueante con RabbitMQ
- **Multicanal** - Soporte para Email, SMS y Web Push notifications
- **Broadcast** - Envío simultáneo a todos los canales con Topic Exchange
- **Bulk Processing** - Envío masivo a múltiples usuarios
- **Alta Disponibilidad** - Configuración de consumers concurrentes (3-10)
- **Retry Policy** - Reintentos automáticos con backoff exponencial
- **SSL/TLS** - Conexión segura a broker en la nube

## 🏗️ Arquitectura

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

## 🛠️ Tech Stack

| Categoría | Tecnología |
|-----------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.2 |
| Messaging | Spring AMQP + RabbitMQ |
| Cloud Broker | CloudAMQP (AWS) |
| Serialization | Jackson (JSON) |
| Utilities | Lombok |
| Documentation | SpringDoc OpenAPI (Swagger) |
| Testing | JUnit 5 + Mockito |

## 📁 Estructura del Proyecto

```
src/main/java/com/notifications/
├── config/
│   ├── RabbitMqConfig.java      # Exchange, Queues, Bindings
│   ├── JacksonConfig.java       # JSON serialization
│   └── SwaggerConfig.java       # API documentation
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

## 🚀 Quick Start

### Prerrequisitos

- Java 17+
- Maven 3.8+

### Ejecutar

```bash
# Clonar repositorio
git clone https://github.com/tu-usuario/notification-system.git
cd notification-system

# Ejecutar aplicación
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

### Swagger UI

Documentación interactiva disponible en:
```
http://localhost:8080/swagger-ui.html
```

## ⚙️ Configuración CloudAMQP

La aplicación se conecta a un broker CloudAMQP mediante variables de entorno (ver `.env.example`) — no se versionan credenciales reales en este repositorio:

```properties
spring.rabbitmq.host=${RABBITMQ_HOST}
spring.rabbitmq.port=${RABBITMQ_PORT:5671}
spring.rabbitmq.username=${RABBITMQ_USERNAME}
spring.rabbitmq.password=${RABBITMQ_PASSWORD}
spring.rabbitmq.virtual-host=${RABBITMQ_VHOST}
spring.rabbitmq.ssl.enabled=true
```

Configurá estas variables en tu entorno local (`.env`) o en tu gestor de secretos (AWS Secrets Manager, HashiCorp Vault, etc.) antes de levantar la app.

## 📡 API Endpoints

### Enviar Notificación Individual

```http
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

```http
POST /api/v1/notifications/broadcast?userId=user123&message=Mensaje importante
```

### Envío Masivo (Bulk)

```http
POST /api/v1/notifications/bulk
Content-Type: application/json

{
  "userIds": ["user1", "user2", "user3"],
  "message": "Promoción especial",
  "channels": ["EMAIL", "SMS"]
}
```

### Health Check

```http
GET /api/v1/notifications/health
```

## 🔀 Message Routing

| Canal | Routing Key | Cola Destino |
|-------|-------------|--------------|
| EMAIL | `notify.email` | notifications.email.queue |
| SMS | `notify.sms` | notifications.sms.queue |
| WEB | `notify.web` | notifications.web.queue |
| ALL | `notify.all` | Todas las colas (broadcast) |

## 🧪 Testing

El proyecto incluye tests unitarios con JUnit 5 y Mockito:

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests con reporte de cobertura
mvn test jacoco:report
```

### Tests Incluidos

| Clase | Cobertura |
|-------|-----------|
| `NotificationControllerTest` | Controllers + validaciones |
| `NotificationServiceTest` | Lógica de negocio + routing |
| `NotificationEventTest` | Domain events |
| `NotificationChannelTest` | Enum routing keys |

## 💡 Demo - Comandos cURL

```powershell
# 📧 EMAIL
curl -X POST http://localhost:8080/api/v1/notifications `
  -H "Content-Type: application/json" `
  -d '{"userId":"user-001","message":"Bienvenido a nuestro servicio","channel":"EMAIL"}'

# 📱 SMS
curl -X POST http://localhost:8080/api/v1/notifications `
  -H "Content-Type: application/json" `
  -d '{"userId":"user-001","message":"Tu codigo es 123456","channel":"SMS"}'

# 🌐 WEB PUSH
curl -X POST http://localhost:8080/api/v1/notifications `
  -H "Content-Type: application/json" `
  -d '{"userId":"user-001","message":"Nueva promocion disponible","channel":"WEB"}'

# 📢 BROADCAST (todos los canales)
curl -X POST http://localhost:8080/api/v1/notifications `
  -H "Content-Type: application/json" `
  -d '{"userId":"user-001","message":"Alerta importante","channel":"ALL"}'

# 📦 BULK (envío masivo)
curl -X POST http://localhost:8080/api/v1/notifications/bulk `
  -H "Content-Type: application/json" `
  -d '{"userIds":["user-001","user-002","user-003"],"message":"Promocion especial","channels":["EMAIL","SMS"]}'
```

## 📈 Configuración de Escalabilidad

| Parámetro | Valor | Descripción |
|-----------|-------|-------------|
| `concurrency` | 3 | Consumers iniciales por cola |
| `max-concurrency` | 10 | Máximo de consumers bajo carga |
| `prefetch` | 10 | Mensajes pre-cargados por consumer |
| `retry.max-attempts` | 3 | Reintentos antes de DLQ |
| `retry.multiplier` | 2.0 | Backoff exponencial |

### Latencia Simulada por Canal

| Canal | Latencia | Simula |
|-------|----------|--------|
| EMAIL | 1500ms | SMTP / SendGrid / SES |
| SMS | 800ms | Twilio / AWS SNS |
| WEB | 500ms | Firebase Cloud Messaging |
