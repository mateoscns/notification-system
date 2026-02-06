package com.notifications.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifications.domain.NotificationChannel;
import com.notifications.domain.NotificationEvent;
import com.notifications.dto.NotificationRequest;
import com.notifications.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        NotificationController controller = new NotificationController(notificationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /api/v1/notifications - should return 202 ACCEPTED")
    void sendNotification_returnsAccepted() throws Exception {
        NotificationRequest request = new NotificationRequest("user-001", "Test message", NotificationChannel.EMAIL);
        NotificationEvent event = NotificationEvent.create("user-001", "Test message", NotificationChannel.EMAIL);

        when(notificationService.sendNotification(any())).thenReturn(event);

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.channel").value("EMAIL"))
                .andExpect(jsonPath("$.eventId").exists());
    }

    @Test
    @DisplayName("POST /api/v1/notifications - should return 400 when userId is blank")
    void sendNotification_validationError_userId() throws Exception {
        NotificationRequest request = new NotificationRequest("", "Test message", NotificationChannel.EMAIL);

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.userId").exists());
    }

    @Test
    @DisplayName("POST /api/v1/notifications - should return 400 when message is blank")
    void sendNotification_validationError_message() throws Exception {
        NotificationRequest request = new NotificationRequest("user-001", "", NotificationChannel.EMAIL);

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.message").exists());
    }

    @Test
    @DisplayName("POST /api/v1/notifications - should return 400 when channel is null")
    void sendNotification_validationError_channel() throws Exception {
        String json = "{\"userId\":\"user-001\",\"message\":\"Test\",\"channel\":null}";

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/notifications - ALL channel should return broadcast response")
    void sendNotification_broadcast() throws Exception {
        NotificationRequest request = new NotificationRequest("user-001", "Broadcast", NotificationChannel.ALL);
        NotificationEvent event = NotificationEvent.create("user-001", "Broadcast", NotificationChannel.ALL);

        when(notificationService.sendNotification(any())).thenReturn(event);

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.channel").value("ALL"));
    }

    @Test
    @DisplayName("GET /api/v1/notifications/health - should return UP status")
    void healthCheck_returnsUp() throws Exception {
        mockMvc.perform(get("/api/v1/notifications/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("notification-system"));
    }
}
