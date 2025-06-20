package com.medicinska.rezervacija.termini_pregledi.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreatedEvent {
    private Long notificationId;
    private Long appointmentId;
    private String correlationId;
    private String message;
}