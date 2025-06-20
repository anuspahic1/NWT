package com.medicinska.rezervacija.termini_pregledi.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationFailedEvent {
    private Long appointmentId;
    private Long terminId;
    private String correlationId;
    private String reason;
}