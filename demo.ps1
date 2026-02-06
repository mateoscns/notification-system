# ============================================================
# Script de Demo - Sistema de Notificaciones Multicanal
# ============================================================
# Ejecutar: .\demo.ps1
# Prerequisito: La aplicacion debe estar corriendo en puerto 8080
# ============================================================

$baseUrl = "http://localhost:8080/api/v1/notifications"

function Send-Notification {
    param([string]$Channel, [string]$Message, [string]$UserId = "user-001")
    
    $body = @{
        userId  = $UserId
        message = $Message
        channel = $Channel
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -ContentType "application/json" -Body $body
    return $response
}

Write-Host ""
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "   DEMO - Sistema de Notificaciones Multicanal" -ForegroundColor Cyan
Write-Host "   RabbitMQ + Spring Boot 3 + CloudAMQP" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

# Health Check
Write-Host "[1/6] Health Check..." -ForegroundColor Yellow
$health = Invoke-RestMethod -Uri "$baseUrl/health" -Method GET
Write-Host "  Status: $($health.status) | Service: $($health.service)" -ForegroundColor Green
Write-Host ""

Start-Sleep -Seconds 1

# Email
Write-Host "[2/6] Enviando notificacion EMAIL..." -ForegroundColor Yellow
$email = Send-Notification -Channel "EMAIL" -Message "Bienvenido a nuestro servicio"
Write-Host "  EventId: $($email.eventId)" -ForegroundColor Green
Write-Host "  Status:  $($email.status) | Channel: $($email.channel)" -ForegroundColor Green
Write-Host ""

Start-Sleep -Seconds 1

# SMS
Write-Host "[3/6] Enviando notificacion SMS..." -ForegroundColor Yellow
$sms = Send-Notification -Channel "SMS" -Message "Tu codigo de verificacion es 483291"
Write-Host "  EventId: $($sms.eventId)" -ForegroundColor Green
Write-Host "  Status:  $($sms.status) | Channel: $($sms.channel)" -ForegroundColor Green
Write-Host ""

Start-Sleep -Seconds 1

# Web Push
Write-Host "[4/6] Enviando notificacion WEB..." -ForegroundColor Yellow
$web = Send-Notification -Channel "WEB" -Message "Nueva promocion disponible"
Write-Host "  EventId: $($web.eventId)" -ForegroundColor Green
Write-Host "  Status:  $($web.status) | Channel: $($web.channel)" -ForegroundColor Green
Write-Host ""

Start-Sleep -Seconds 1

# Broadcast
Write-Host "[5/6] Enviando BROADCAST (todos los canales)..." -ForegroundColor Yellow
$broadcast = Send-Notification -Channel "ALL" -Message "Alerta importante para todos los usuarios"
Write-Host "  EventId: $($broadcast.eventId)" -ForegroundColor Green
Write-Host "  Status:  $($broadcast.status) | Channel: $($broadcast.channel)" -ForegroundColor Green
Write-Host "  Message: $($broadcast.message)" -ForegroundColor Green
Write-Host ""

Start-Sleep -Seconds 1

# Bulk
Write-Host "[6/6] Enviando notificacion MASIVA (3 usuarios x 2 canales)..." -ForegroundColor Yellow
$bulkBody = @{
    userIds  = @("user-001", "user-002", "user-003")
    message  = "Promocion especial para clientes premium"
    channels = @("EMAIL", "SMS")
} | ConvertTo-Json

$bulk = Invoke-RestMethod -Uri "$baseUrl/bulk" -Method POST -ContentType "application/json" -Body $bulkBody
Write-Host "  BatchId: $($bulk.batchId)" -ForegroundColor Green
Write-Host "  Total:   $($bulk.totalNotifications) notificaciones encoladas" -ForegroundColor Green
Write-Host "  Canales: $($bulk.channels -join ', ')" -ForegroundColor Green
Write-Host ""

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "   DEMO COMPLETADA" -ForegroundColor Cyan
Write-Host "   Revisa los logs de la aplicacion para ver" -ForegroundColor Cyan
Write-Host "   el procesamiento asincrono de cada canal" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""
