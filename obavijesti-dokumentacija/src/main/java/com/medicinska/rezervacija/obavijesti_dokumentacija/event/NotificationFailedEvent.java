package com.medicinska.rezervacija.obavijesti_dokumentacija.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationFailedEvent {
    private Long appointmentId; // ID pregleda za koji je notifikacija trebala biti kreirana
    private Long terminId; // ID termina koji treba resetovati (ako je potreban)
    private String correlationId;
    private String reason; // Razlog neuspjeha
}